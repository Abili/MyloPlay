package com.abig.myloplay;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u0015B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005J\b\u0010\t\u001a\u00020\nH\u0016J\u001c\u0010\u000b\u001a\u00020\f2\n\u0010\r\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u000e\u001a\u00020\nH\u0016J\u001c\u0010\u000f\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\nH\u0016J\u0014\u0010\u0013\u001a\u00020\f2\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0014R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/abig/myloplay/OthersAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/abig/myloplay/OthersAdapter$PlaylistViewHolder;", "fragmentManager", "Landroidx/fragment/app/FragmentManager;", "(Landroidx/fragment/app/FragmentManager;)V", "playlists", "", "Lcom/abig/myloplay/Playlist;", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "setPlaylists", "", "PlaylistViewHolder", "app_debug"})
public final class OthersAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.abig.myloplay.OthersAdapter.PlaylistViewHolder> {
    @org.jetbrains.annotations.NotNull
    private final androidx.fragment.app.FragmentManager fragmentManager = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.abig.myloplay.Playlist> playlists = null;
    
    public OthersAdapter(@org.jetbrains.annotations.NotNull
    androidx.fragment.app.FragmentManager fragmentManager) {
        super();
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public com.abig.myloplay.OthersAdapter.PlaylistViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull
    com.abig.myloplay.OthersAdapter.PlaylistViewHolder holder, int position) {
    }
    
    @java.lang.Override
    public int getItemCount() {
        return 0;
    }
    
    public final void setPlaylists(@org.jetbrains.annotations.NotNull
    java.util.List<com.abig.myloplay.Playlist> playlists) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\b\u0010\u0013\u001a\u00020\u0012H\u0002J\u0018\u0010\u0014\u001a\u00020\f2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0016H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/abig/myloplay/OthersAdapter$PlaylistViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lcom/abig/myloplay/databinding/OthersPlaylistItemBinding;", "(Lcom/abig/myloplay/OthersAdapter;Lcom/abig/myloplay/databinding/OthersPlaylistItemBinding;)V", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "othersProfileImage", "Landroid/widget/ImageView;", "playlistName", "Landroid/widget/TextView;", "bind", "", "playlist", "Lcom/abig/myloplay/Playlist;", "getColorDrawable", "Landroid/graphics/drawable/BitmapDrawable;", "color", "", "getRandomColor", "retrieveOtherUserPlaylists", "playlistId", "", "uid", "app_debug"})
    public final class PlaylistViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull
        private final com.abig.myloplay.databinding.OthersPlaylistItemBinding binding = null;
        @org.jetbrains.annotations.NotNull
        private final android.widget.TextView playlistName = null;
        @org.jetbrains.annotations.NotNull
        private final android.widget.ImageView othersProfileImage = null;
        @org.jetbrains.annotations.NotNull
        private final com.google.firebase.auth.FirebaseAuth auth = null;
        
        public PlaylistViewHolder(@org.jetbrains.annotations.NotNull
        com.abig.myloplay.databinding.OthersPlaylistItemBinding binding) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull
        com.abig.myloplay.Playlist playlist) {
        }
        
        private final void retrieveOtherUserPlaylists(java.lang.String playlistId, java.lang.String uid) {
        }
        
        private final int getRandomColor() {
            return 0;
        }
        
        private final android.graphics.drawable.BitmapDrawable getColorDrawable(int color) {
            return null;
        }
    }
}