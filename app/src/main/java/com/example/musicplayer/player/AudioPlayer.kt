package com.example.musicplayer.player

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.io.IOException


abstract class AudioPlayer (context: Context, musicUrl: String) {

    private var mediaPlayer: MediaPlayer? = null
    private val TAG = "SimpleMusicPlayer"

    /**
     * 播放指定地址的音乐。
     * 地址可以是：
     * 1. 设备的本地文件路径 (如: "/sdcard/Music/song.mp3")
     * 2. 网络上的有效音乐文件 URL (如: "http://example.com/stream/song.mp3")
     *
     * @param musicPathOrUrl 音乐文件的地址。
     */
    fun play(musicPathOrUrl: String) {
        // 1. 释放旧的播放器实例，确保每次播放都是新的开始
        release()

        mediaPlayer = MediaPlayer()

        try {
            // 2. 设置数据源 (核心步骤)
            mediaPlayer?.setDataSource(musicPathOrUrl)

            // 3. 设置准备完成监听器：一旦播放器准备好，立即开始播放
            mediaPlayer?.setOnPreparedListener { player ->
                player.start()
                Log.i(TAG, "音乐已开始播放: $musicPathOrUrl")
            }

            // 4. 异步准备 (对于网络流或大文件是必须的，避免阻塞 UI)
            mediaPlayer?.prepareAsync()
            Log.d(TAG, "正在异步准备播放器...")

        } catch (e: IOException) {
            Log.e(TAG, "加载音乐文件失败 (IO 错误): ${e.message}")
            release()
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "加载音乐文件失败 (地址或格式错误): ${e.message}")
            release()
        }
    }

    /**
     * 暂停播放
     */
    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            Log.d(TAG, "播放暂停")
        }
    }

    /**
     * 停止并重置播放器
     */
    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        Log.d(TAG, "播放停止并重置")
    }

    /**
     * 释放 MediaPlayer 资源（防止内存泄漏和占用硬件资源）
     */
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d(TAG, "播放器资源已释放")
    }

}