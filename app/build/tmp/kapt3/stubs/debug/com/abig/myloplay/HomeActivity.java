package com.abig.myloplay;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 $2\u00020\u00012\u00020\u0002:\u0001$B\u0005\u00a2\u0006\u0002\u0010\u0003J\u0018\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u00152\b\u0010\u0017\u001a\u0004\u0018\u00010\u0006H\u0002J\"\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001b2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0006H\u0014J\u0012\u0010\u001d\u001a\u00020\u00192\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0014J\u0010\u0010 \u001a\u00020\u00192\u0006\u0010!\u001a\u00020\"H\u0016J\b\u0010#\u001a\u00020\u0019H\u0002R\u0016\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006%"}, d2 = {"Lcom/abig/myloplay/HomeActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "Lcom/abig/myloplay/AddPlaylistDialogFragment$AddPlaylistDialogListener;", "()V", "activityResultLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "adapter", "Lcom/abig/myloplay/OwnersAdapter;", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "binding", "Lcom/abig/myloplay/databinding/ActivityHomeBinding;", "database", "Lcom/google/firebase/database/FirebaseDatabase;", "imageUri", "Landroidx/compose/runtime/MutableState;", "Landroid/net/Uri;", "getImageUri", "()Landroidx/compose/runtime/MutableState;", "getSelectedAudioFiles", "", "", "data", "onActivityResult", "", "requestCode", "", "resultCode", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onPlaylistCreated", "playlist", "Lcom/abig/myloplay/Playlist;", "openAudioFiles", "Companion", "app_debug"})
public final class HomeActivity extends androidx.appcompat.app.AppCompatActivity implements com.abig.myloplay.AddPlaylistDialogFragment.AddPlaylistDialogListener {
    private com.abig.myloplay.databinding.ActivityHomeBinding binding;
    private com.google.firebase.auth.FirebaseAuth auth;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState<android.net.Uri> imageUri = null;
    private com.google.firebase.database.FirebaseDatabase database;
    private androidx.activity.result.ActivityResultLauncher<android.content.Intent> activityResultLauncher;
    private com.abig.myloplay.OwnersAdapter adapter;
    @org.jetbrains.annotations.NotNull()
    public static final com.abig.myloplay.HomeActivity.Companion Companion = null;
    private static final int REQUEST_CODE_SELECT_SONGS = 3;
    private static final java.lang.String TAG = "HomeActivity";
    
    public HomeActivity() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.compose.runtime.MutableState<android.net.Uri> getImageUri() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void openAudioFiles() {
    }
    
    @java.lang.Override()
    protected void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable()
    android.content.Intent data) {
    }
    
    private final java.util.List<java.lang.String> getSelectedAudioFiles(android.content.Intent data) {
        return null;
    }
    
    @java.lang.Override()
    public void onPlaylistCreated(@org.jetbrains.annotations.NotNull()
    com.abig.myloplay.Playlist playlist) {
    }
    
    @kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/abig/myloplay/HomeActivity$Companion;", "", "()V", "REQUEST_CODE_SELECT_SONGS", "", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}