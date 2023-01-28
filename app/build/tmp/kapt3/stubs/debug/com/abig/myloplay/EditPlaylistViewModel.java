package com.abig.myloplay;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u000eJ\u000e\u0010\u0011\u001a\u00020\u001d2\u0006\u0010\u0002\u001a\u00020\u0003J\u000e\u0010\u001e\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u000eJ\u000e\u0010\u001f\u001a\u00020\u001b2\u0006\u0010\u0002\u001a\u00020 J\u000e\u0010!\u001a\u00020\u001b2\u0006\u0010\u000f\u001a\u00020\u000bR\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00108F\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\r0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u001d\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\u00108F\u00a2\u0006\u0006\u001a\u0004\b\u0019\u0010\u0012\u00a8\u0006\""}, d2 = {"Lcom/abig/myloplay/EditPlaylistViewModel;", "Landroidx/lifecycle/ViewModel;", "playlistId", "", "repository", "Lcom/abig/myloplay/PlaylistRepository;", "context", "Landroid/content/Context;", "(JLcom/abig/myloplay/PlaylistRepository;Landroid/content/Context;)V", "_playlist", "Landroidx/lifecycle/MutableLiveData;", "Lcom/abig/myloplay/Playlist;", "_songs", "", "Lcom/abig/myloplay/Song;", "playlist", "Landroidx/lifecycle/LiveData;", "getPlaylist", "()Landroidx/lifecycle/LiveData;", "playlistDao", "Lcom/abig/myloplay/PlaylistDao;", "selectedSongIds", "getSelectedSongIds", "()Landroidx/lifecycle/MutableLiveData;", "songs", "getSongs", "addSong", "", "song", "Lcom/abig/myloplay/PlaylistResult;", "removeSong", "start", "", "updatePlaylist", "app_debug"})
public final class EditPlaylistViewModel extends androidx.lifecycle.ViewModel {
    private final com.abig.myloplay.PlaylistRepository repository = null;
    private final android.content.Context context = null;
    private final androidx.lifecycle.MutableLiveData<com.abig.myloplay.Playlist> _playlist = null;
    private final com.abig.myloplay.PlaylistDao playlistDao = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.util.List<java.lang.Long>> selectedSongIds = null;
    private final androidx.lifecycle.MutableLiveData<java.util.List<com.abig.myloplay.Song>> _songs = null;
    
    public EditPlaylistViewModel(long playlistId, @org.jetbrains.annotations.NotNull()
    com.abig.myloplay.PlaylistRepository repository, @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.util.List<java.lang.Long>> getSelectedSongIds() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.abig.myloplay.Playlist> getPlaylist() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.abig.myloplay.Song>> getSongs() {
        return null;
    }
    
    public final void start(@org.jetbrains.annotations.NotNull()
    java.lang.String playlistId) {
    }
    
    public final void addSong(@org.jetbrains.annotations.NotNull()
    com.abig.myloplay.Song song) {
    }
    
    public final void removeSong(@org.jetbrains.annotations.NotNull()
    com.abig.myloplay.Song song) {
    }
    
    public final void updatePlaylist(@org.jetbrains.annotations.NotNull()
    com.abig.myloplay.Playlist playlist) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.abig.myloplay.PlaylistResult getPlaylist(long playlistId) {
        return null;
    }
}