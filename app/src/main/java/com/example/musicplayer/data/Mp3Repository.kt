package com.example.musicplayer.data

abstract class Mp3Repository {
    abstract fun getMp3List(): List<Mp3Info>
    abstract fun getMp3Lyric(mp3Path: String): List<Mp3Lyric>
    abstract fun getSongList(): List<Song>
    abstract fun getBg_videoList(): List<bg_video>

}



