package com.example.musicplayer.data

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

