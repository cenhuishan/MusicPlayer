package com.example.musicplayer.data

import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.State // 检查 Animatable.asState() 的导入
import kotlinx.coroutines.flow.Flow

/**
 * 监听 currentPlayTime。
 *
 * 根据 currentPlayTime 查找当前处于 startTime 和 endTime 之间的 LyricLine。
 *
 * 如果找到新的活跃行，则更新 AnimatedLyricState，并设置 triggerTime。
 *
 * 如果没有活跃行，则设置 isActive = false。
 */
interface LyricViewModel {

    // VM1. 实时播放时间流
    val currentPlayTime: StateFlow<Long>

    // VM2. 所有歌词数据
    val allLyrics: State<List<LyricLine>>

    // VM3. 核心输出：当前活跃歌词行及其动画状态
    // Compose View 只需观察这个状态来渲染
    val currentAnimatedLyric: StateFlow<AnimatedLyricState>

    // VM4. 暴露给 View 的接口（如启动/停止播放）
    fun playMusic()
}


// VM5. 动画状态数据模型（传递给 View）
// 2. 状态模型 (V)
data class AnimatedLyricState(
    val lyric: LyricLine = LyricLine(0, 0, ""), // 默认值
    val isActive: Boolean = false,
    val triggerTime: Long? = null
)

