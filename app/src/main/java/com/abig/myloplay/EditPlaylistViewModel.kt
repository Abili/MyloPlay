package com.abig.myloplay

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.*
class EditPlaylistViewModel(
    playlistId: Long,
    private val repository: PlaylistRepository,
    private val context: Context
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    private val playlistDao = AppDatabase.getInstance(context).playlistDao()
    val selectedSongIds = MutableLiveData<List<Long>>()
    val playlist: LiveData<Playlist>
        get() = _playlist

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>>
        get() = _songs

    fun start(playlistId: String) {
        viewModelScope.launch {
            val playlist = repository.getPlaylist(playlistId)
            _playlist.value = playlist
            _songs.value = repository.getSongs(playlist!!.songIds)
        }
    }

    fun addSong(song: Song) {
        viewModelScope.launch {
            val playlist = _playlist.value!!
            val songIds = playlist.songIds.toMutableList()
            songIds.add(song.id.toString())
            playlist.songIds = songIds
            repository.updatePlaylist(playlist)
        }
    }

    fun removeSong(song: Song) {
        viewModelScope.launch {
            val playlist = _playlist.value!!
            val songIds = playlist.songIds.toMutableList()
            songIds.remove(song.id.toString())
            playlist.songIds = songIds
            repository.updatePlaylist(playlist)
        }
    }
    fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    fun getPlaylist(playlistId: Long): PlaylistResult {
        return try {
            val playlist = playlistDao.getPlaylist(playlistId.toString())
            if (playlist != null) {
                PlaylistResult.Success(playlist)
            } else {
                PlaylistResult.Error("Playlist not found")
            }
        } catch (e: Exception) {
            PlaylistResult.Error(e.message ?: "Unknown error")
        }
    }
}
