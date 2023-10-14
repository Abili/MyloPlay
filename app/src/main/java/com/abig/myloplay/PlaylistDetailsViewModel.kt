package com.abig.myloplay

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    class Factory(private val playlistRepository: PlaylistRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlaylistDetailsViewModel::class.java)) {
                return PlaylistDetailsViewModel(playlistRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist>
        get() = _playlist

    private val _songList = MutableLiveData<List<Song>>()
    val songList: LiveData<List<Song>>
        get() = _songList

    fun loadPlaylist(playlistId: String) {
        viewModelScope.launch {
            val playlist = playlistRepository.getPlaylist(playlistId)
            if (playlist != null) {
                _playlist.value = playlist
                _songList.value = playlistRepository.getSongs(playlist.songIds!!)
            }
        }
    }
}

