package com.example.musicplayer


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.musicplayer.player.AudioPlayerImpl
import java.io.File


// Kotlin 示例
class MainActivity : AppCompatActivity() {


    // 定义请求码，用于在回调中识别请求
    private  val STORAGE_PERMISSION_CODE = 1
    private lateinit var videoView: VideoView
    private lateinit var clickButton: Button
    // 获取 Music 文件夹的 File 对象
    lateinit var musicDirectory: File
    private lateinit var audioPlayer: AudioPlayerImpl
    // 获取 Music 文件夹的绝对路径 (String)
    lateinit var  musicPath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val isOk = checkAndRequestStoragePermissions(this)

        clickButton = findViewById(R.id.btn_enter)

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
             mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
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



    /**
     * 检查并请求 READ/WRITE_EXTERNAL_STORAGE 权限。
     *
     * @return Boolean 权限是否已经授予。
     */
    fun checkAndRequestStoragePermissions(activity: Activity): Boolean {
        // 检查 READ_EXTERNAL_STORAGE 权限
        val readPermission = ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        // 检查 WRITE_EXTERNAL_STORAGE 权限 (在 Android 10+ 版本中，通常不需要此权限来读取文件)
        val writePermission = ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // 1. 如果权限都已经授予，则返回 true
        if (readPermission == PackageManager.PERMISSION_GRANTED &&
            writePermission == PackageManager.PERMISSION_GRANTED) {

            return true
        }
        // 2. 如果有权限未授予，则请求权限
        else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
            // 此时权限尚未授予，请求已发出，返回 false
            return false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ✅ 权限已授予！
                // 在这里执行你之前想在 onCreate 中执行的操作：
                // 例如：loadMusicFiles()
                // Log.d("MainActivity", "权限获取成功，开始加载音乐")
                 musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                   audioPlayer = AudioPlayerImpl( this, musicDirectory.name)
                // 获取 Music 文件夹的绝对路径 (String)
                 musicPath = musicDirectory.absolutePath
                clickButton.setOnClickListener {
                    audioPlayer.play(musicPath+"/覃诚芳-还没有爱够.mp3")
                }

            } else {
                // ❌ 权限被拒绝
                // 提示用户或禁用依赖权限的功能
                // Log.w("MainActivity", "权限被拒绝")
               // openAppSettings(this)
            }
        }
    }

    /**
     * 跳转到应用设置页面，让用户手动开启权限
     */
    fun openAppSettings(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    }
}