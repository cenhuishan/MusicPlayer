import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.AnimatedLyricState
import com.example.musicplayer.data.LyricLine
import com.example.musicplayer.data.LyricViewModel
import com.example.musicplayer.data.MusicPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


// 假设 MusicPlayer 是一个注入的依赖
class LyricViewModelImpl(
    private val player: MusicPlayer // 注入的音乐播放器
) : ViewModel(), LyricViewModel {

    // --- VM 状态定义 ---

    // VM1. 实时播放时间流 (直接映射播放器时间)
    private val _currentPlayTime = MutableStateFlow(0L)
    override val currentPlayTime: StateFlow<Long> = _currentPlayTime.asStateFlow()

    // VM2. 所有歌词数据 (使用 Compose State 管理静态数据)
    // 注意：如果您在 ViewModel 中使用 Compose State，ViewModel 必须依赖 Compose Runtime 库
    override val allLyrics: State<List<LyricLine>> = mutableStateOf(player.currentSongLyrics)

    // VM3. 核心输出：当前活跃歌词行及其动画状态
    private val _currentAnimatedLyric = MutableStateFlow(AnimatedLyricState())
    override val currentAnimatedLyric: StateFlow<AnimatedLyricState> = _currentAnimatedLyric.asStateFlow()

    // 用于追踪上一次活跃的歌词行，以便判断是否为“新的”活跃行
    private var lastActiveLyricLine: LyricLine? = null

    // 用于管理时间监听 Job
    private var playbackJob: Job? = null


    init {
        // 自动启动监听播放时间
        observePlaybackTime()
    }

    // --- 业务逻辑实现 ---

    private fun observePlaybackTime() {
        // 取消旧的 Job (如果有)
        playbackJob?.cancel()

        // 在 ViewModel Scope 中启动新的 Job 监听播放时间
        playbackJob = player.playbackTime
            .onEach { time ->
                _currentPlayTime.value = time
                updateAnimatedLyricState(time)
            }
            .launchIn(viewModelScope) // Flow 监听在 ViewModel 生命周期内持续运行
    }


    private fun updateAnimatedLyricState(currentTime: Long) {
        // 1. 查找当前活跃的歌词行
        val currentLine = allLyrics.value.find {
            currentTime >= it.startTime && currentTime < it.endTime
        }
        Log.d("LyricViewModel", "Current Line: $currentLine")
        Log.d("LyricViewModel", "CurrentTime: $currentTime")




        // 2. 判断状态是否变化
        if (currentLine != null) {
            // A. 找到了活跃行
            val isNewActiveLine = currentLine != lastActiveLyricLine

            val newState = AnimatedLyricState(
                lyric = currentLine,
                isActive = true,
                // 只有在新行激活时，才设置 triggerTime
                triggerTime = if (isNewActiveLine) currentTime else _currentAnimatedLyric.value.triggerTime
            )

            _currentAnimatedLyric.value = newState
            lastActiveLyricLine = currentLine

        } else {
            // B. 没有活跃行 (例如，歌曲开头或结尾的间歇)
            val newState = AnimatedLyricState(
                lyric = _currentAnimatedLyric.value.lyric, // 保持上一句歌词文本
                isActive = false, // 关闭动画
                triggerTime = null
            )
            _currentAnimatedLyric.value = newState
            lastActiveLyricLine = null
        }
    }


    // VM4. 暴露给 View 的接口
    override fun playMusic() {
        player.start()
    }

    // 清理资源，虽然 Flow Job 会随 viewModelScope 自动取消，但显式定义析构函数是一个好习惯
    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
    }
}


// --- 模拟 MusicPlayer 实现 (用于测试) ---

class MockMusicPlayer(
    val lyricData: List<LyricLine>
) : MusicPlayer {
    private val _playbackTime = MutableStateFlow(0L)
    override val playbackTime: Flow<Long> = _playbackTime.asStateFlow()
    override val currentSongLyrics: List<LyricLine> = lyricData

    private var playerJob: Job? = null

    override fun start() {
        if (playerJob?.isActive == true) return

        playerJob = CoroutineScope(Dispatchers.Default).launch {
            // 模拟时间从 0ms 递增
            var time = 0L
            val endTime =  22500 // 假设总时长 1000秒

            while (time <= endTime) {
                _playbackTime.value = time
                delay(50L) // 每 50ms 更新一次时间
                time += 50L
                if (time >=22500) {
                    time = 0L
                }
            }
        }
    }
}