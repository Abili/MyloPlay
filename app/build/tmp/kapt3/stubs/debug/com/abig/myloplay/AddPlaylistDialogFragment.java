package com.abig.myloplay;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0007\u0018\u0000 \u001a2\u00020\u0001:\u0002\u0019\u001aB\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\u0012\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0016J\u0016\u0010\u0016\u001a\u00020\u000f2\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u0003H\u0002R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0003X\u0082.\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lcom/abig/myloplay/AddPlaylistDialogFragment;", "Landroidx/fragment/app/DialogFragment;", "songs", "", "Lcom/abig/myloplay/Song;", "(Ljava/util/List;)V", "binding", "Lcom/abig/myloplay/databinding/DialogAddPlaylistBinding;", "downloadUrls", "Landroid/net/Uri;", "imageUri", "Landroidx/compose/runtime/MutableState;", "listener", "Lcom/abig/myloplay/AddPlaylistDialogFragment$AddPlaylistDialogListener;", "onAttach", "", "context", "Landroid/content/Context;", "onCreateDialog", "Landroid/app/Dialog;", "savedInstanceState", "Landroid/os/Bundle;", "uploadImage", "downloadUrl", "", "AddPlaylistDialogListener", "Companion", "app_debug"})
public final class AddPlaylistDialogFragment extends androidx.fragment.app.DialogFragment {
    private java.util.List<com.abig.myloplay.Song> songs;
    private final androidx.compose.runtime.MutableState<android.net.Uri> imageUri = null;
    private java.util.List<? extends android.net.Uri> downloadUrls;
    private com.abig.myloplay.AddPlaylistDialogFragment.AddPlaylistDialogListener listener;
    private com.abig.myloplay.databinding.DialogAddPlaylistBinding binding;
    @org.jetbrains.annotations.NotNull
    public static final com.abig.myloplay.AddPlaylistDialogFragment.Companion Companion = null;
    
    public AddPlaylistDialogFragment(@org.jetbrains.annotations.NotNull
    java.util.List<com.abig.myloplay.Song> songs) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public android.app.Dialog onCreateDialog(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    private final void uploadImage(java.util.List<java.lang.String> downloadUrl) {
    }
    
    @java.lang.Override
    public void onAttach(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
    }
    
    @kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0006"}, d2 = {"Lcom/abig/myloplay/AddPlaylistDialogFragment$AddPlaylistDialogListener;", "", "onPlaylistCreated", "", "playlist", "Lcom/abig/myloplay/Playlist;", "app_debug"})
    public static abstract interface AddPlaylistDialogListener {
        
        public abstract void onPlaylistCreated(@org.jetbrains.annotations.NotNull
        com.abig.myloplay.Playlist playlist);
    }
    
    @kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a8\u0006\n"}, d2 = {"Lcom/abig/myloplay/AddPlaylistDialogFragment$Companion;", "", "()V", "newInstance", "Lcom/abig/myloplay/AddPlaylistDialogFragment;", "context", "Landroid/content/Context;", "songIds", "", "Lcom/abig/myloplay/Song;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.abig.myloplay.AddPlaylistDialogFragment newInstance(@org.jetbrains.annotations.NotNull
        android.content.Context context, @org.jetbrains.annotations.NotNull
        java.util.List<com.abig.myloplay.Song> songIds) {
            return null;
        }
    }
}