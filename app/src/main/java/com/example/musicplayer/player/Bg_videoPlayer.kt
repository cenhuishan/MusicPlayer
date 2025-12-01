package com.example.musicplayer.player

import com.example.musicplayer.data.bg_video

abstract class Bg_videoPlayer {
    var videos : List<bg_video> = emptyList()
    var currentVideoIndex: Int = 0
    var currentVideo: bg_video? = null


     fun next_() {
         currentVideoIndex = (currentVideoIndex + 1) % videos.size
         currentVideo = videos[currentVideoIndex]

     }
     fun prev_() {
         if (currentVideoIndex == 0) {
             currentVideoIndex = videos.size - 1
         } else {
             currentVideoIndex = (currentVideoIndex - 1 + videos.size) % videos.size
             currentVideo = videos[currentVideoIndex]

         }
     }
}