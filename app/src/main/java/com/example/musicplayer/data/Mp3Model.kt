package com.example.musicplayer.data

import kotlinx.coroutines.flow.Flow

data class Mp3Lyric(val line :String,
                     val startTime: Long,
                     val endTime: Long,
                     val lineNumber: Int)
data class Mp3Info(val title: String,
                   val artist: String,
                   val album: String)

data class Song(val name:String,
                val path:String,
                val duration:Long)

data class bg_video(val name:String,
                val path:String)

//歌词数据 模型
data class LyricLine(val startTime: Long,
                      val endTime: Long,
                      val text: String)

// 4. 假设的播放器接口 (模拟音乐播放)
interface MusicPlayer {
    val playbackTime: Flow<Long> // 实时时间流
    val currentSongLyrics: List<LyricLine>
    fun start()
}

