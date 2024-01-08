package com.abig.myloplay

data class Recommendation(
    val songId:String?="",
    val songUrl: String = "",
    val songTitle: String = "",
    val artist: String = "",
    val duration: String = "",
    val recommendedBy: String = "" // Assuming you want to store who recommended the song
)