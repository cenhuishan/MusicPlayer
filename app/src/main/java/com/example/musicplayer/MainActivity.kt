package com.example.musicplayer


import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity


// Kotlin 示例
class MainActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById(R.id.background_video_view)

        // 1. 设置视频路径
        // 假设视频文件名为 'app_background.mp4'，放在 res/raw 目录下
        val videoPath = "android.resource://" + packageName + "/" + R.raw.app_backround
        val uri = Uri.parse(videoPath)

        videoView.setVideoURI(uri)

        // 2. 移除媒体控制器 (重要：背景视频不需要播放/暂停按钮)
        videoView.setMediaController(null)

        // 3. 设置循环播放
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true // 视频准备好后设置循环
            // 调整视频缩放以填充屏幕 (可选，防止黑边)
            // mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
        }

        // 4. 启动播放
        videoView.start()

    }

    // 5. 生命周期管理 (重要：在 Activity 暂停时停止播放，恢复时继续播放)
    override fun onResume() {
        super.onResume()
        if (!videoView.isPlaying) {
            videoView.start()
        }
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.stopPlayback()
    }
}