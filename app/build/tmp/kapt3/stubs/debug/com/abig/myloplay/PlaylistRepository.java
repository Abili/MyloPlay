package com.abig.myloplay;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0019\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\tJ\u0010\u0010\n\u001a\u0004\u0018\u00010\b2\u0006\u0010\u000b\u001a\u00020\fJ\u0012\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u000f0\u000eJ\u001a\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u000f2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\f0\u000fJ\u0019\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\tJ\u000e\u0010\u0014\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0015"}, d2 = {"Lcom/abig/myloplay/PlaylistRepository;", "", "playlistDao", "Lcom/abig/myloplay/PlaylistDao;", "(Lcom/abig/myloplay/PlaylistDao;)V", "deletePlaylist", "", "playlist", "Lcom/abig/myloplay/Playlist;", "(Lcom/abig/myloplay/Playlist;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPlaylist", "id", "", "getPlaylists", "Landroidx/lifecycle/LiveData;", "", "getSongs", "Lcom/abig/myloplay/Song;", "songIds", "savePlaylist", "updatePlaylist", "app_debug"})
public final class PlaylistRepository {
    private final com.abig.myloplay.PlaylistDao playlistDao = null;
    
    public PlaylistRepository(@org.jetbrains.annotations.NotNull
    com.abig.myloplay.PlaylistDao playlistDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<java.util.List<com.abig.myloplay.Playlist>> getPlaylists() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.abig.myloplay.Playlist getPlaylist(@org.jetbrains.annotations.NotNull
    java.lang.String id) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object savePlaylist(@org.jetbrains.annotations.NotNull
    com.abig.myloplay.Playlist playlist, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object deletePlaylist(@org.jetbrains.annotations.NotNull
    com.abig.myloplay.Playlist playlist, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.abig.myloplay.Song> getSongs(@org.jetbrains.annotations.NotNull
    java.util.List<java.lang.String> songIds) {
        return null;
    }
    
    public final void updatePlaylist(@org.jetbrains.annotations.NotNull
    com.abig.myloplay.Playlist playlist) {
    }
}