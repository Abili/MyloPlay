package com.abig.myloplay;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\u0018\u0000 62\u00020\u0001:\u00016B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010&\u001a\u00020\u00052\u0006\u0010\'\u001a\u00020\u000fH\u0002J\u0012\u0010(\u001a\u00020)2\b\u0010*\u001a\u0004\u0018\u00010+H\u0014J\b\u0010,\u001a\u00020)H\u0014J\b\u0010-\u001a\u00020)H\u0002J\u001c\u0010.\u001a\u00020)2\f\u0010/\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u00100\u001a\u00020\u000fJ\b\u00101\u001a\u00020)H\u0002J\b\u00102\u001a\u00020)H\u0002J\b\u00103\u001a\u00020)H\u0002J\b\u00104\u001a\u00020)H\u0002J\b\u00105\u001a\u00020)H\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0014\u001a\u00020\u000fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u001cX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020 X\u0082.\u00a2\u0006\u0002\n\u0000R\u0016\u0010!\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020#X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u00067"}, d2 = {"Lcom/abig/myloplay/MyloPlayer;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "artists", "", "", "audioListAdapter", "Lcom/abig/myloplay/AudioListAdapter;", "getAudioListAdapter", "()Lcom/abig/myloplay/AudioListAdapter;", "setAudioListAdapter", "(Lcom/abig/myloplay/AudioListAdapter;)V", "binding", "Lcom/abig/myloplay/databinding/ActivityMyloPlayerBinding;", "currentPosition", "", "handler", "Landroid/os/Handler;", "isPlaying", "", "lastPlaybackPosition", "getLastPlaybackPosition", "()I", "setLastPlaybackPosition", "(I)V", "lyricsApiClient", "Lcom/abig/myloplay/LyricsApiClient;", "lyricsTextView", "Landroid/widget/TextView;", "mediaPlayer", "Landroid/media/MediaPlayer;", "playButton", "Landroid/widget/ImageView;", "playlist", "seekBar", "Landroid/widget/SeekBar;", "songDuration", "songTitles", "formatDuration", "duration", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "playCurrentSong", "playShuffledSongs", "shuffledPlaylist", "startPosition", "playSong", "skipToNextSong", "skipToPreviousSong", "updateSeekBar", "updateUIForCurrentSong", "Companion", "app_debug"})
public final class MyloPlayer extends androidx.appcompat.app.AppCompatActivity {
    private com.abig.myloplay.databinding.ActivityMyloPlayerBinding binding;
    private android.media.MediaPlayer mediaPlayer;
    private android.widget.ImageView playButton;
    private android.widget.SeekBar seekBar;
    private boolean isPlaying = false;
    @org.jetbrains.annotations.NotNull
    private final android.os.Handler handler = null;
    @org.jetbrains.annotations.Nullable
    private java.util.List<java.lang.String> playlist;
    private int currentPosition = 0;
    private java.util.List<java.lang.String> songTitles;
    private java.util.List<java.lang.String> artists;
    private java.lang.String songDuration;
    private android.widget.TextView lyricsTextView;
    private com.abig.myloplay.LyricsApiClient lyricsApiClient;
    private int lastPlaybackPosition = 0;
    public com.abig.myloplay.AudioListAdapter audioListAdapter;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String ARTIST = "";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String SONG_ID = "userID";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String SONG_TITLE = "title";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String SONG_DURATION = "duration";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String PLAYLIST = "playlist";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String CURRENT_POSITION = "current_position";
    @org.jetbrains.annotations.NotNull
    public static final com.abig.myloplay.MyloPlayer.Companion Companion = null;
    
    public MyloPlayer() {
        super();
    }
    
    public final int getLastPlaybackPosition() {
        return 0;
    }
    
    public final void setLastPlaybackPosition(int p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.abig.myloplay.AudioListAdapter getAudioListAdapter() {
        return null;
    }
    
    public final void setAudioListAdapter(@org.jetbrains.annotations.NotNull
    com.abig.myloplay.AudioListAdapter p0) {
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void playSong() {
    }
    
    private final void updateSeekBar() {
    }
    
    private final java.lang.String formatDuration(int duration) {
        return null;
    }
    
    private final void skipToNextSong() {
    }
    
    private final void skipToPreviousSong() {
    }
    
    private final void playCurrentSong() {
    }
    
    private final void updateUIForCurrentSong() {
    }
    
    public final void playShuffledSongs(@org.jetbrains.annotations.NotNull
    java.util.List<java.lang.String> shuffledPlaylist, int startPosition) {
    }
    
    @java.lang.Override
    protected void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/abig/myloplay/MyloPlayer$Companion;", "", "()V", "ARTIST", "", "CURRENT_POSITION", "PLAYLIST", "SONG_DURATION", "SONG_ID", "SONG_TITLE", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}