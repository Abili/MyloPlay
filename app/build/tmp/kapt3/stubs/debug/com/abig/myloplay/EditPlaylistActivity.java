package com.abig.myloplay;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0014J\b\u0010\r\u001a\u00020\nH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/abig/myloplay/EditPlaylistActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/abig/myloplay/databinding/ActivityEditPlaylistBinding;", "playlist", "Lcom/abig/myloplay/Playlist;", "viewModel", "Lcom/abig/myloplay/EditPlaylistViewModel;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "savePlaylist", "Companion", "app_debug"})
public final class EditPlaylistActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.abig.myloplay.databinding.ActivityEditPlaylistBinding binding;
    private com.abig.myloplay.EditPlaylistViewModel viewModel;
    private com.abig.myloplay.Playlist playlist;
    @org.jetbrains.annotations.NotNull
    public static final com.abig.myloplay.EditPlaylistActivity.Companion Companion = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String EXTRA_PLAYLIST_ID = "playlist_id";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String EXTRA_PLAYLIST = "EXTRA_PLAYLIST";
    
    public EditPlaylistActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void savePlaylist() {
    }
    
    @kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/abig/myloplay/EditPlaylistActivity$Companion;", "", "()V", "EXTRA_PLAYLIST", "", "EXTRA_PLAYLIST_ID", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}