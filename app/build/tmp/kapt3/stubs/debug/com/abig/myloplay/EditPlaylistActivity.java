package com.abig.myloplay;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 \u00112\u00020\u0001:\u0001\u0011B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\t\u001a\u00020\nH\u0002J\u0012\u0010\u000b\u001a\u00020\n2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0014J\b\u0010\u000e\u001a\u00020\nH\u0002J\u0010\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/abig/myloplay/EditPlaylistActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/abig/myloplay/databinding/ActivityEditPlaylistBinding;", "playlistId", "", "songsAdapter", "Lcom/abig/myloplay/EditPlaylistSongsAdapter;", "loadPlaylistDetails", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "savePlaylistChanges", "showToast", "message", "Companion", "app_debug"})
public final class EditPlaylistActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String EXTRA_PLAYLIST_ID = "extra_playlist_id";
    private java.lang.String playlistId;
    private com.abig.myloplay.databinding.ActivityEditPlaylistBinding binding;
    private com.abig.myloplay.EditPlaylistSongsAdapter songsAdapter;
    @org.jetbrains.annotations.NotNull
    public static final com.abig.myloplay.EditPlaylistActivity.Companion Companion = null;
    
    public EditPlaylistActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void loadPlaylistDetails() {
    }
    
    private final void savePlaylistChanges() {
    }
    
    private final void showToast(java.lang.String message) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/abig/myloplay/EditPlaylistActivity$Companion;", "", "()V", "EXTRA_PLAYLIST_ID", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}