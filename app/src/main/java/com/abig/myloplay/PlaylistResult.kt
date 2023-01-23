package com.abig.myloplay

sealed class PlaylistResult {
    data class Success(val playlist: Playlist) : PlaylistResult()
    data class Error(val message: String) : PlaylistResult()
}

