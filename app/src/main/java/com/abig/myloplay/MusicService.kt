package com.abig.myloplay

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder

class MusicService : Service() {

    private val binder = MusicBinder()
    lateinit var mediaPlayer: MediaPlayer
    private lateinit var notificationManager: MusicNotificationManager
    private var playlist: List<String> = listOf()
    var currentPosition: Int = 0
    lateinit var songTitles: List<String>
    lateinit var artists: List<String>
    private var callback: MusicServiceCallback? = null
    private val handler = Handler()

    override fun onCreate() {
        super.onCreate()
        initializeMediaPlayer()
        notificationManager = MusicNotificationManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_PLAY -> resumeSong()
                ACTION_PAUSE -> pauseSong()
                ACTION_NEXT -> skipToNextSong()
                ACTION_PREVIOUS -> skipToPreviousSong()
                else -> {
                    playlist = it.getStringArrayListExtra(PLAYLIST)!!
                    songTitles = it.getStringArrayListExtra(SONG_TITLE)!!
                    artists = it.getStringArrayListExtra(ARTIST)!!
                    currentPosition = it.getIntExtra(CURRENT_POSITION, 0)
                    playCurrentSong(playlist, currentPosition)
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun initializeMediaPlayer() {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(audioAttributes)
        mediaPlayer.setOnCompletionListener {
            skipToNextSong()
        }
    }

    fun playCurrentSong(playlist: List<String>, position: Int) {
        this.playlist = playlist
        this.currentPosition = position
        if (playlist.isNotEmpty() && currentPosition in playlist.indices) {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(playlist[currentPosition])
            mediaPlayer.setOnPreparedListener { mp ->
                mp.start()
                notificationManager.showNotification(
                    songTitles[currentPosition],
                    artists[currentPosition]
                )
                updateProgress()
                callback?.onSongChanged(songTitles[currentPosition], artists[currentPosition])
            }
            mediaPlayer.prepareAsync()
        }
    }

    fun pauseSong() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            notificationManager.updateNotification(false)
            callback?.onPlaybackStateChanged(false)
        }
    }

    fun resumeSong() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            notificationManager.updateNotification(true)
            callback?.onPlaybackStateChanged(true)
            updateProgress()
        }
    }

    fun skipToNextSong() {
        currentPosition = (currentPosition + 1) % playlist.size
        playCurrentSong(playlist, currentPosition)
    }

    fun skipToPreviousSong() {
        currentPosition = if (currentPosition - 1 < 0) playlist.size - 1 else currentPosition - 1
        playCurrentSong(playlist, currentPosition)
    }

    fun isPlaying() = mediaPlayer.isPlaying

    fun registerCallback(callback: MusicServiceCallback) {
        this.callback = callback
    }

    fun unregisterCallback(callback: MusicServiceCallback) {
        if (this.callback == callback) {
            this.callback = null
        }
    }

    private fun updateProgress() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isPlaying()) {
                    callback?.onProgressChanged(mediaPlayer.currentPosition, mediaPlayer.duration)
                    handler.postDelayed(this, 1000)
                }
            }
        }, 0)
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    companion object {
        const val ACTION_PLAY = "com.abig.myloplay.ACTION_PLAY"
        const val ACTION_PAUSE = "com.abig.myloplay.ACTION_PAUSE"
        const val ACTION_NEXT = "com.abig.myloplay.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.abig.myloplay.ACTION_PREVIOUS"
        const val PLAYLIST = "playlist"
        const val SONG_TITLE = "song_title"
        const val ARTIST = "artist"
        const val CURRENT_POSITION = "current_position"
    }

    interface MusicServiceCallback {
        fun onSongChanged(title: String, artist: String)
        fun onPlaybackStateChanged(isPlaying: Boolean)
        fun onProgressChanged(currentPosition: Int, duration: Int)
    }
}
