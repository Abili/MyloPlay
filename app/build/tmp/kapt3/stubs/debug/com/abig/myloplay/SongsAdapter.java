package com.abig.myloplay;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0016B\u0005\u00a2\u0006\u0002\u0010\u0003J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0006J\u0014\u0010\n\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\u000bJ\u0006\u0010\f\u001a\u00020\bJ\b\u0010\r\u001a\u00020\u000eH\u0016J\u0018\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\u00022\u0006\u0010\u0011\u001a\u00020\u000eH\u0016J\u0018\u0010\u0012\u001a\u00020\u00022\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u000eH\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/abig/myloplay/SongsAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/abig/myloplay/SongsAdapter$PlaylistViewHolder;", "()V", "playlists", "", "Lcom/abig/myloplay/Song;", "add", "", "song", "addAll", "", "clear", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "PlaylistViewHolder", "app_debug"})
public final class SongsAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.abig.myloplay.SongsAdapter.PlaylistViewHolder> {
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.abig.myloplay.Song> playlists = null;
    
    public SongsAdapter() {
        super();
    }
    
    public final void addAll(@org.jetbrains.annotations.NotNull
    java.util.List<com.abig.myloplay.Song> song) {
    }
    
    public final void add(@org.jetbrains.annotations.NotNull
    com.abig.myloplay.Song song) {
    }
    
    public final void clear() {
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public com.abig.myloplay.SongsAdapter.PlaylistViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override
    public int getItemCount() {
        return 0;
    }
    
    @java.lang.Override
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull
    com.abig.myloplay.SongsAdapter.PlaylistViewHolder holder, int position) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/abig/myloplay/SongsAdapter$PlaylistViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "(Landroid/view/View;)V", "binding", "Lcom/abig/myloplay/databinding/SongItemBinding;", "currentIndex", "", "bind", "", "song", "Lcom/abig/myloplay/Song;", "app_debug"})
    public static final class PlaylistViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull
        private final com.abig.myloplay.databinding.SongItemBinding binding = null;
        private int currentIndex = -1;
        
        public PlaylistViewHolder(@org.jetbrains.annotations.NotNull
        android.view.View itemView) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull
        com.abig.myloplay.Song song) {
        }
    }
}