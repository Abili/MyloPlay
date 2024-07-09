package com.abig.myloplay

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlayListViewModel(private val playlistRepository: PlaylistRepository) : ViewModel() {
    //private val playlistRepository = PlaylistRepository(context)
    private val _myplaylists = MutableLiveData<List<Playlist>>()
    private val _groupPlaylists = MutableLiveData<List<Playlist>>()

    val myplaylists: LiveData<List<Playlist>> get() = _myplaylists
    val groupPlaylists: LiveData<List<Playlist>> get() = _groupPlaylists

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlayListViewModel::class.java)) {
                val repository = PlaylistRepository(context)
                return PlayListViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    // Function to load the current user's playlists
    fun loadMyPlaylists() {
        playlistRepository.retrieveCurrentUserPlaylists { playlists ->
            _groupPlaylists.value = playlists
        }
    }

    // Function to load playlists of other users
    fun loadGroupPlaylists() {
        playlistRepository.retrieveGroupPlaylists { playlists ->
            _groupPlaylists.value = playlists
        }
    }
}
