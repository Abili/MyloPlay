package com.abig.myloplay

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MiniPlayerService : Service(), AudioManager.OnAudioFocusChangeListener {

    private val binder = MiniPlayerBinder()
    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler()
    var isPlaying = false
    private lateinit var playlist: List<String>
    private var currentPosition = 0
    private lateinit var songTitles: List<String>
    private lateinit var artists: List<String>
    private lateinit var songDuration: List<String>
    private lateinit var audioManager: AudioManager
    private lateinit var audioFocusRequest: AudioFocusRequest

    override fun onCreate() {
        super.onCreate()
        initializeMediaPlayer()
        initializeAudioFocusRequest()
        // Initialize songTitles here if needed
        songTitles = emptyList()
    }

    private fun initializeMediaPlayer() {
        mediaPlayer = MediaPlayer()
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        mediaPlayer.setAudioAttributes(audioAttributes)
    }

    private fun initializeAudioFocusRequest() {
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(this)
                .build()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun initialize(
        playlist: List<String>,
        startPosition: Int,
        songTitles: List<String>,
        artists: List<String>,
        songDuration: List<String>
    ) {
        this.playlist = playlist
        this.currentPosition = startPosition
        this@MiniPlayerService.songTitles = songTitles
        this.artists = artists
        this.songDuration = songDuration
    }

    private fun playCurrentSong(playlist: List<String>, currentPosition: Int) {
        if (currentPosition >= 0 && currentPosition < playlist.size) {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(playlist[currentPosition].toString())

            mediaPlayer.setOnPreparedListener { mp ->
                mp.start()
                //binding.smallPlayer.playBtn.setImageResource(R.drawable.pause)
                //binding.miniPlayBtn.setImageResource(R.drawable.pause)
                isPlaying = true
                updateSeekBar()
            }

            mediaPlayer.prepareAsync()
        }
        updateNotification()
    }

    fun togglePlayPause() {
        if (isPlaying) {
            pauseMediaPlayer()
        } else {
            startMediaPlayer()
        }
        updateNotification()
    }

    private fun startMediaPlayer() {
        mediaPlayer.start()
        isPlaying = true
        updateSeekBar()
    }

    private fun pauseMediaPlayer() {
        mediaPlayer.pause()
        isPlaying = false
    }

    fun skipToNextSong() {
        currentPosition = (currentPosition + 1) % playlist.size
        playCurrentSong()
    }

    fun skipToPreviousSong() {
        currentPosition = if (currentPosition == 0) {
            playlist.size - 1
        } else {
            currentPosition - 1
        }
        playCurrentSong()
    }

    private fun playCurrentSong() {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(playlist[currentPosition])
        mediaPlayer.prepareAsync()
    }

    private fun updateSeekBar() {
        if (mediaPlayer.isPlaying) {
            handler.postDelayed({
                updateSeekBar()
            }, 1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
        abandonAudioFocus()
    }

    inner class MiniPlayerBinder : Binder() {
        fun getService(): MiniPlayerService = this@MiniPlayerService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requestAudioFocus()
        createNotificationAndStartForeground()
        return START_NOT_STICKY
    }

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(audioFocusRequest)
        } else {
            audioManager.requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    private fun createNotificationAndStartForeground() {
        val notification = createNotification(songTitles)
        startForeground(FOREGROUND_SERVICE_ID, notification)
    }

    private fun createNotification(songTitles: List<String>): Notification {
        val notificationIntent = Intent(this, AudioActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "YourChannelId")
            .setContentTitle("Now Playing")
            .setContentText(getCurrentSongTitle())
            .setSmallIcon(R.drawable.myloplay_logo)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.previous,
                "Previous",
                getPendingIntentForAction(ACTION_PREVIOUS)
            )
            .addAction(
                if (isPlaying) R.drawable.pause else R.drawable.play,
                if (isPlaying) "Pause" else "Play",
                getPendingIntentForAction(ACTION_TOGGLE_PLAY_PAUSE)
            )
            .addAction(
                R.drawable.next,
                "Next",
                getPendingIntentForAction(ACTION_NEXT)
            )
            .build()
    }


    private fun getCurrentSongTitle(): String {
        return if (currentPosition < songTitles.size) {
            songTitles[currentPosition]
        } else {
            "Unknown Song"
        }
    }



    private fun getPendingIntentForAction(action: String): PendingIntent {
        val intent = Intent(this, MiniPlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun updateNotification() {
        val notification = createNotification(songTitles)
        startForeground(FOREGROUND_SERVICE_ID, notification)
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
        } else {
            audioManager.abandonAudioFocus(this)
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        // Handle audio focus changes if needed
    }

    companion object {
        const val FOREGROUND_SERVICE_ID = 101
        const val ACTION_TOGGLE_PLAY_PAUSE = "com.abig.myloplay.action.TOGGLE_PLAY_PAUSE"
        const val ACTION_PREVIOUS = "com.abig.myloplay.action.PREVIOUS"
        const val ACTION_NEXT = "com.abig.myloplay.action.NEXT"
    }
}
