package com.abig.myloplay;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0001\u0015B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\f8F\u00a2\u0006\u0006\u001a\u0004\b\r\u0010\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\f8F\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\u000e\u00a8\u0006\u0016"}, d2 = {"Lcom/abig/myloplay/PlaylistDetailsViewModel;", "Landroidx/lifecycle/ViewModel;", "playlistRepository", "Lcom/abig/myloplay/PlaylistRepository;", "(Lcom/abig/myloplay/PlaylistRepository;)V", "_playlist", "Landroidx/lifecycle/MutableLiveData;", "Lcom/abig/myloplay/Playlist;", "_songList", "", "Lcom/abig/myloplay/Song;", "playlist", "Landroidx/lifecycle/LiveData;", "getPlaylist", "()Landroidx/lifecycle/LiveData;", "songList", "getSongList", "loadPlaylist", "", "playlistId", "", "Factory", "app_debug"})
public final class PlaylistDetailsViewModel extends androidx.lifecycle.ViewModel {
    private final com.abig.myloplay.PlaylistRepository playlistRepository = null;
    private final androidx.lifecycle.MutableLiveData<com.abig.myloplay.Playlist> _playlist = null;
    private final androidx.lifecycle.MutableLiveData<java.util.List<com.abig.myloplay.Song>> _songList = null;
    
    public PlaylistDetailsViewModel(@org.jetbrains.annotations.NotNull
    com.abig.myloplay.PlaylistRepository playlistRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<com.abig.myloplay.Playlist> getPlaylist() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<java.util.List<com.abig.myloplay.Song>> getSongList() {
        return null;
    }
    
    public final void loadPlaylist(@org.jetbrains.annotations.NotNull
    java.lang.String playlistId) {
    }
    
    @kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J%\u0010\u0005\u001a\u0002H\u0006\"\b\b\u0000\u0010\u0006*\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u0002H\u00060\tH\u0016\u00a2\u0006\u0002\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/abig/myloplay/PlaylistDetailsViewModel$Factory;", "Landroidx/lifecycle/ViewModelProvider$Factory;", "playlistRepository", "Lcom/abig/myloplay/PlaylistRepository;", "(Lcom/abig/myloplay/PlaylistRepository;)V", "create", "T", "Landroidx/lifecycle/ViewModel;", "modelClass", "Ljava/lang/Class;", "(Ljava/lang/Class;)Landroidx/lifecycle/ViewModel;", "app_debug"})
    public static final class Factory implements androidx.lifecycle.ViewModelProvider.Factory {
        private final com.abig.myloplay.PlaylistRepository playlistRepository = null;
        
        public Factory(@org.jetbrains.annotations.NotNull
        com.abig.myloplay.PlaylistRepository playlistRepository) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        @kotlin.Suppress(names = {"UNCHECKED_CAST"})
        @java.lang.Override
        public <T extends androidx.lifecycle.ViewModel>T create(@org.jetbrains.annotations.NotNull
        java.lang.Class<T> modelClass) {
            return null;
        }
    }
}