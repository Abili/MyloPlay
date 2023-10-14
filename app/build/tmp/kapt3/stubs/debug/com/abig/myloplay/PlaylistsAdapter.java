package com.abig.myloplay;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0014B\u0005\u00a2\u0006\u0002\u0010\u0003J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0006J\u0006\u0010\n\u001a\u00020\bJ\b\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u0010\r\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\fH\u0016J\u0018\u0010\u0010\u001a\u00020\u00022\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\fH\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/abig/myloplay/PlaylistsAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/abig/myloplay/PlaylistsAdapter$PlaylistViewHolder;", "()V", "playlists", "", "Lcom/abig/myloplay/Playlist;", "add", "", "playlist", "clear", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "PlaylistViewHolder", "app_debug"})
public final class PlaylistsAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.abig.myloplay.PlaylistsAdapter.PlaylistViewHolder> {
    private final java.util.List<com.abig.myloplay.Playlist> playlists = null;
    
    public PlaylistsAdapter() {
        super();
    }
    
    public final void add(@org.jetbrains.annotations.NotNull
    com.abig.myloplay.Playlist playlist) {
    }
    
    public final void clear() {
    }
    
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public com.abig.myloplay.PlaylistsAdapter.PlaylistViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override
    public int getItemCount() {
        return 0;
    }
    
    @java.lang.Override
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull
    com.abig.myloplay.PlaylistsAdapter.PlaylistViewHolder holder, int position) {
    }
    
    @kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/abig/myloplay/PlaylistsAdapter$PlaylistViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "(Landroid/view/View;)V", "binding", "Lcom/abig/myloplay/databinding/PlaylistItemBinding;", "bind", "", "playlist", "Lcom/abig/myloplay/Playlist;", "app_debug"})
    public static final class PlaylistViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private final com.abig.myloplay.databinding.PlaylistItemBinding binding = null;
        
        public PlaylistViewHolder(@org.jetbrains.annotations.NotNull
        android.view.View itemView) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull
        com.abig.myloplay.Playlist playlist) {
        }
    }
}