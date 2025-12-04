package com.example.musicplayer.ui

import LyricViewModelImpl
import MockMusicPlayer
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.data.AnimatedLyricState
import androidx.compose.runtime.State // 检查 Animatable.asState() 的导入
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.musicplayer.data.LyricLine
import com.example.musicplayer.data.LyricViewModel
import kotlin.random.Random
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.ui.unit.TextUnit

@Composable
fun LyricEffectView(state: AnimatedLyricState, modifier: Modifier = Modifier) {
    // 1. 定义目标状态（活跃 vs 非活跃）
    val targetFontSize = if (state.isActive) 32.sp else 20.sp
    val targetColor = if (state.isActive) Color(0xFF1DB954) else Color.Gray
    val targetAlpha = if (state.isActive) 1.0f else 0.5f // 增加透明度变化，让它淡入淡出

    // 2. 使用 animate*AsState 实现平滑过渡
// 核心修正：使用 animateValueAsState

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(300),
        label = "ColorTransition"
    )
    val animatedAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(300),
        label = "AlphaTransition"
    )

    // 3. 渲染 Composable
    val textToShow = state.lyric.text // 始终显示当前歌词文本

    // 关键：不再使用 if/else 切换 Composable，而是用一个 Composable 改变其属性。
    if (state.isActive) {
        // 如果是活跃状态，使用 ActiveLyricText 渲染其复杂动画
        ActiveLyricText(
            lyric = textToShow,
            triggerTime = state.triggerTime!!,
            highlightColor = animatedColor, // 使用平滑过渡的颜色
            modifier = modifier
        )
    }
    //    else {
//        // 如果是非活跃状态，使用普通的 Text 渲染过渡效果
//        Text(
//            text = textToShow,
//            fontSize = animatedFontSize,
//            color = animatedColor,
//            modifier = modifier
//                .padding(vertical = 8.dp)
//                .graphicsLayer { alpha = animatedAlpha } // 应用淡入淡出
//        )
//    }
}

/**
 * LyricLine(startTime: Long, endTime: Long, text: String)
 * 时间单位为毫秒 (ms)
 */
val mockLyric: List<LyricLine> = listOf(
    // 第一段：节奏启动
    LyricLine(0L, 1000L, "（Intro）"), // 1.0s
    LyricLine(1200L, 2300L, "当代码被点燃"), // 1.1s
    LyricLine(2500L, 3800L, "旋律在指尖流转"), // 1.3s
    LyricLine(4000L, 5000L, "第一个特效，启动"), // 1.0s

    // 第二段：核心特效展示
    LyricLine(5500L, 6800L, "看歌词，在中心旋转"), // 1.3s
    LyricLine(7000L, 8200L, "强烈的动感，一秒完成"), // 1.2s
    LyricLine(8500L, 9500L, "但律动，永不停止"), // 1.0s
    LyricLine(9700L, 10700L, "放大，又缩小"), // 1.0s

    // 第三段：副歌节奏加快
    LyricLine(10900L, 11600L, "Compose的力量"), // 0.7s
    LyricLine(11800L, 12500L, "动画的战场"), // 0.7s
    LyricLine(12700L, 13600L, "每一个瞬间都精准捕捉"), // 0.9s
    LyricLine(13800L, 14700L, "状态与视图完美分离"), // 0.9s

    // 第四段：情感和技术总结
    LyricLine(15000L, 16000L, "MVVM 架构清晰"), // 1.0s
    LyricLine(16200L, 17200L, "Flows 驱动一切"), // 1.0s
    LyricLine(17400L, 18200L, "你的需求，我的实现"), // 0.8s
    LyricLine(18400L, 19000L, "（Chorus）"), // 0.6s

    // 第五段：结尾和收尾
    LyricLine(19200L, 19800L, "视觉震撼"), // 0.6s
    LyricLine(20000L, 20600L, "体验升级"), // 0.6s
    LyricLine(20800L, 21500L, "完美的播放器"), // 0.7s
    LyricLine(21700L, 22500L, "（Outro）"), // 0.8s
)

@Preview
@Composable
fun previewLyricEffectView() {
    // 实例化 ViewModel 和 Mock Player (注意：MockMusicPlayer 和 ViewModelImpl 需要在 Preview 范围外有合适的生命周期)
    // ⚠️ 注意：Preview 环境下，ViewModel 的创建和 MockPlayer 的 Job 需要注意线程和生命周期问题。
    val player = remember { MockMusicPlayer(mockLyric) }
    val viewModel = remember { LyricViewModelImpl(player) }

    // 核心修正：将 StateFlow 转换为 Compose State
    val state by viewModel.currentAnimatedLyric.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()){
        // 使用 State 值
        Box(modifier = Modifier.fillMaxSize()) {
            // 歌词没有居中，这里给它一个居中 Modifier，方便预览
            LyricEffectView(
                state = state,
                modifier = Modifier.align(Alignment.Center)
            )

            // 启动播放器
            LaunchedEffect(Unit) {
                viewModel.playMusic()
            }
        }
    }
}


//律动动画
@Composable
fun rememberPulsingScale(): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "PulsingTransition")

    // 缩放值在 0.95f (缩小) 和 1.05f (放大) 之间无限循环
    return infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 600, // 律动的周期，例如 600ms
                easing = FastOutSlowInEasing // 带来自然加速和减速的律动感
            ),
            repeatMode = RepeatMode.Reverse // 反转，实现 1.0 -> 1.05 -> 1.0 的循环
        ),
        label = "ScaleAnimation"
    )
}


@Composable
fun ActiveLyricText(
    lyric: String,
    triggerTime: Long, // 从 ViewModel 传入的激活时间戳
    highlightColor: Color = Color(0xFF1DB954),
    modifier: Modifier = Modifier // 确保接收 Modifier
) {
    // 1. 律动缩放值 (无限循环)
    val pulsingScaleState = rememberPulsingScale()

    // 2. 旋转角度值 (一次性 0 -> 720度)
    val rotationState = rememberOneTimeRotation(triggerTime = triggerTime)

    // 3. 🆕 一次性放大值 (1.0 -> 1.2)
    val growScaleState = rememberOneTimeGrowScale(triggerTime = triggerTime, targetScale = 1.2f)

    // 组合特效
    Text(
        text = lyric,
        fontSize = 32.sp,
        fontWeight = FontWeight.Light,
        color = highlightColor,
        modifier = modifier
            .padding(vertical = 8.dp)
            .graphicsLayer {
                // 应用旋转角度
                rotationZ = rotationState.value

                // 🌟 核心：将两种缩放效果相乘
                val combinedScale = pulsingScaleState.value * growScaleState.value

                scaleX = combinedScale
                scaleY = combinedScale

                // 确保变换是基于文本的中心点
                transformOrigin = TransformOrigin.Center
            }
    )
}


@Composable
fun rememberOneTimeRotation(triggerTime: Long): State<Float> {
    // 使用 Animatable 来控制旋转角度
    val rotation = remember { Animatable(0f) }

    // 使用 LaunchedEffect 确保动画只在 triggerTime 变化时执行一次
    LaunchedEffect(triggerTime) {
        // 从当前角度 (0f) 开始，快速旋转到 target (例如 720度)
        rotation.animateTo(
            targetValue = Random(64).nextInt(720).toFloat(), // 旋转两圈
            animationSpec = tween(
                durationMillis = 1000, // 持续 1秒
                easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f) // 快速开始，平滑减速停止
            )
        )
        // 动画完成后，旋转角度停留在 720度（但由于 Modifier 绕圈，视觉上与 0度相同）
    }
    return rotation.asState()
}

@Composable
fun rememberOneTimeGrowScale(triggerTime: Long, targetScale: Float = 2.5f): State<Float> {
    // 使用 Animatable 来控制缩放值
    // 初始值设为 1.0f (原始大小)
    val growScale = remember { Animatable(1.0f) }

    // 使用 LaunchedEffect 确保动画在 triggerTime 变化时（即新行激活时）执行一次
    LaunchedEffect(triggerTime) {
        // 从 1.0f 快速放大到 targetScale
        growScale.animateTo(
            targetValue = targetScale,
            animationSpec = tween(
                durationMillis = 1000, // 持续 1秒，与旋转动画同步
                easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f) // 快速开始，平滑减速
            )
        )
        // 动画完成后，缩放值停留在 targetScale，直到歌词行结束
    }
    return growScale.asState()
}