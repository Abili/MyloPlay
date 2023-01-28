package com.abig.myloplay;

import java.lang.System;

@androidx.room.Dao()
@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0019\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u0012\u0010\u0007\u001a\u0004\u0018\u00010\u00052\u0006\u0010\b\u001a\u00020\tH\'J\u0014\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\f0\u000bH\'J\u001c\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\f2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\t0\fH\'J\u0019\u0010\u0010\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\'\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0012"}, d2 = {"Lcom/abig/myloplay/PlaylistDao;", "", "deletePlaylist", "", "playlist", "Lcom/abig/myloplay/Playlist;", "(Lcom/abig/myloplay/Playlist;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPlaylist", "id", "", "getPlaylists", "Landroidx/lifecycle/LiveData;", "", "getSongs", "Lcom/abig/myloplay/Song;", "songIds", "insertPlaylist", "updatePlaylist", "app_debug"})
public abstract interface PlaylistDao {
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM playlists")
    public abstract androidx.lifecycle.LiveData<java.util.List<com.abig.myloplay.Playlist>> getPlaylists();
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Query(value = "SELECT * FROM playlists WHERE id = :id")
    public abstract com.abig.myloplay.Playlist getPlaylist(@org.jetbrains.annotations.NotNull()
    java.lang.String id);
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Insert()
    public abstract java.lang.Object insertPlaylist(@org.jetbrains.annotations.NotNull()
    com.abig.myloplay.Playlist playlist, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation);
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Delete()
    public abstract java.lang.Object deletePlaylist(@org.jetbrains.annotations.NotNull()
    com.abig.myloplay.Playlist playlist, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation);
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM songs WHERE id IN (:songIds)")
    public abstract java.util.List<com.abig.myloplay.Song> getSongs(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> songIds);
    
    @androidx.room.Update()
    public abstract void updatePlaylist(@org.jetbrains.annotations.NotNull()
    com.abig.myloplay.Playlist playlist);
}