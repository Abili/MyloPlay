package com.abig.myloplay

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.MiniPlayerBinding

class MiniPlayerActivity : AppCompatActivity(), MusicService.MusicServiceCallback {

    private lateinit var binding: MiniPlayerBinding
    private lateinit var musicService: MusicService
    private var isBound = false
    private val handler = Handler()
    private var playlist: List<String> = listOf()
    private var currentPosition: Int = 0
    private lateinit var songTitles: List<String>
    private lateinit var artists: List<String>
    private lateinit var songDuration: List<String>
    private lateinit var songLists: List<AudioFile>
    private lateinit var adapter: MiniPlayerAdapter
    private lateinit var audioManager: AudioManager
    @RequiresApi(Build.VERSION_CODES.O)
    private var audioFocus = AudioManager.AUDIOFOCUS_NONE

    private val serviceConnection = object : ServiceConnection {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            musicService.registerCallback(this@MiniPlayerActivity)
            isBound = true

            // Request audio focus when connecting to the service
            requestAudioFocus()

            musicService.playCurrentSong(playlist, currentPosition)

            setupControls()
            updateUIForCurrentSong()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MiniPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize AudioManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        playlist = intent.getStringArrayListExtra(PLAYLIST)!!
        songTitles = intent.getStringArrayListExtra(SONG_TITLE)!!
        artists = intent.getStringArrayListExtra(ARTIST)!!
        songDuration = intent.getStringArrayListExtra(SONG_DURATION)!!
        songLists = intent.getParcelableArrayListExtra(SONG_LISTS)!!
        currentPosition = intent.getIntExtra(CURRENT_POSITION, 0)

        adapter = MiniPlayerAdapter(this) { position ->
            if (position != currentPosition) {
                currentPosition = position
                musicService.playCurrentSong(playlist, currentPosition)
                updateUIForCurrentSong()
                binding.pauseBtn.visibility = View.VISIBLE
                binding.playBtn.visibility = View.GONE
            } else {
                if (musicService.isPlaying()) {
                    musicService.pauseSong()
                    binding.pauseBtn.visibility = View.GONE
                    binding.playBtn.visibility = View.VISIBLE
                } else {
                    musicService.resumeSong()
                }
            }
        }
        adapter.setData(songLists)

        binding.playerRecycler.layoutManager = LinearLayoutManager(this)
        binding.playerRecycler.adapter = adapter

        val intent = Intent(this, MusicService::class.java).apply {
            putStringArrayListExtra(MusicService.PLAYLIST, ArrayList(playlist))
            putStringArrayListExtra(MusicService.SONG_TITLE, ArrayList(songTitles))
            putStringArrayListExtra(MusicService.ARTIST, ArrayList(artists))
            putExtra(MusicService.CURRENT_POSITION, currentPosition)
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        startService(intent)
    }

    private fun setupControls() {
        binding.pauseBtn.setOnClickListener {
            if (isBound) {
                if (musicService.isPlaying()) {
                    musicService.pauseSong()
                    binding.playBtn.visibility = View.VISIBLE
                    binding.pauseBtn.visibility = View.GONE
                } else {
                    musicService.resumeSong()
                }
            }
        }
        binding.nextBtn.setOnClickListener {
            if (isBound) {
                musicService.skipToNextSong()
                updateUIForCurrentSong()
            }
        }
        binding.prevBtn.setOnClickListener {
            if (isBound) {
                musicService.skipToPreviousSong()
                updateUIForCurrentSong()
            }
        }

        binding.playBtn.setOnClickListener {
            if (isBound) {
                if (!musicService.isPlaying()) {
                    musicService.resumeSong()
                    binding.pauseBtn.visibility = View.VISIBLE
                    binding.playBtn.visibility = View.GONE
                } else {
                    musicService.pauseSong()
                }
            }
        }
    }

    private fun updateUIForCurrentSong() {
        if (currentPosition in playlist.indices) {
            val title = songTitles[currentPosition]
            val artist = artists[currentPosition]
            binding.songTitle.text = title
            binding.songArtist.text = artist
        }
        updateElapsedTime()
    }

    private fun updateElapsedTime() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isBound && musicService.isPlaying()) {
                    val elapsedTime = musicService.mediaPlayer.currentPosition
                    binding.startSongDuration.text = formatDuration(elapsedTime)
                    binding.seekBar.progress = elapsedTime
                    handler.postDelayed(this, 1000)
                }
            }
        }, 0)
    }

    private fun formatDuration(duration: Int): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            musicService.unregisterCallback(this)
            unbindService(serviceConnection)
            isBound = false
        }
        abandonAudioFocus() // Abandon audio focus when activity is destroyed
    }

    override fun onSongChanged(title: String, artist: String) {
        binding.songTitle.text = title
        binding.songArtist.text = artist
    }

    override fun onPlaybackStateChanged(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playBtn.visibility = View.GONE
            binding.pauseBtn.visibility = View.VISIBLE
        } else {
            binding.playBtn.visibility = View.VISIBLE
            binding.pauseBtn.visibility = View.GONE
        }
    }

    override fun onProgressChanged(currentPosition: Int, duration: Int) {
        binding.seekBar.max = duration
        binding.seekBar.progress = currentPosition
        binding.startSongDuration.text = formatDuration(currentPosition)
        binding.endSongDuration.text = formatDuration(duration)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestAudioFocus() {
        val result = audioManager.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            audioFocus = AudioManager.AUDIOFOCUS_GAIN
        } else {
            audioFocus = AudioManager.AUDIOFOCUS_NONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun abandonAudioFocus() {
        audioManager.abandonAudioFocus(audioFocusChangeListener)
        audioFocus = AudioManager.AUDIOFOCUS_NONE
    }

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Pause playback
                if (musicService.isPlaying()) {
                    musicService.pauseSong()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Pause playback temporarily
                if (musicService.isPlaying()) {
                    musicService.pauseSong()
                }
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                // Resume playback
                if (!musicService.isPlaying()) {
                    musicService.resumeSong()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Lower volume temporarily
                musicService.mediaPlayer.setVolume(0.2f, 0.2f)
            }
        }
    }

    companion object {
        const val PLAYLIST = "playlist"
        const val CURRENT_POSITION = "current_position"
        const val SONG_TITLE = "song_title"
        const val ARTIST = "artist"
        const val NOTIFICATION_ID = 1
        const val ACTION_PLAY = "com.abig.myloplay.ACTION_PLAY"
        const val ACTION_PAUSE = "com.abig.myloplay.ACTION_PAUSE"
        const val ACTION_NEXT = "com.abig.myloplay.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.abig.myloplay.ACTION_PREVIOUS"
        const val ACTION_SEEK_TO = "com.abig.myloplay.ACTION_SEEK_TO"
        const val SEEK_TO_POSITION = "seek_to_position"
        const val SONG_DURATION = "song_duration"
        const val SONG_LISTS = "song_lists"
    }
}
