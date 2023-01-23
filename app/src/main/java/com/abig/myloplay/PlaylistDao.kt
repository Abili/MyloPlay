package com.abig.myloplay

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlists")
    fun getPlaylists(): LiveData<List<Playlist>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    fun getPlaylist(id: String): Playlist?

    @Insert
    suspend fun insertPlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("SELECT * FROM songs WHERE id IN (:songIds)")
    fun getSongs(songIds: List<String>): List<Song>

    @Update
    fun updatePlaylist(playlist: Playlist)
}

