package com.abig.myloplay

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abig.myloplay.databinding.ActivityMyloPlayerBinding

class MyloPlayer : AppCompatActivity() {
    private lateinit var binding: ActivityMyloPlayerBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playButton: ImageView
    private lateinit var seekBar: SeekBar
    private var isPlaying = false
    private val handler = Handler()
    private var playlist: List<String>? = null
    private var currentPosition: Int = 0
    private lateinit var songTitles: List<String>
    private lateinit var artists: List<String>
    private lateinit var songDuration: String
    private lateinit var lyricsTextView: TextView
    private lateinit var lyricsApiClient: LyricsApiClient

    // ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyloPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playButton = binding.playBtn
        seekBar = binding.seekbar
        lyricsApiClient = LyricsApiClient(binding.lyricsTextView)

        // Initialize the MediaPlayer with audio attributes
        val audioAttributes =
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA).build()

        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(audioAttributes)

        mediaPlayer.setOnPreparedListener {
            // Media player is prepared, you can set the data source here.
            updateSeekBar()
            playCurrentSong()
        }

        mediaPlayer.setOnCompletionListener {
            skipToNextSong()
        }

        playlist = intent.getStringArrayListExtra(PLAYLIST)
        currentPosition = intent.getIntExtra(CURRENT_POSITION, 0)

        songTitles = intent.getStringArrayListExtra(SONG_TITLE)!!
        artists = intent.getStringArrayListExtra(ARTIST)!!
        songDuration = intent.getStringExtra(SONG_DURATION)!!

        updateUIForCurrentSong()

        var lastPlaybackPosition: Int = 0

        playButton.setOnClickListener {
            if (isPlaying) {
                lastPlaybackPosition = mediaPlayer.currentPosition
                mediaPlayer.pause()
                playButton.setImageResource(R.drawable.play)
            } else {
                mediaPlayer.seekTo(lastPlaybackPosition)
                mediaPlayer.start()
                playButton.setImageResource(R.drawable.pause)
                updateSeekBar()
            }
            isPlaying = !isPlaying
        }


        binding.nextBtn.setOnClickListener {
            skipToNextSong()
        }

        binding.prevBtn.setOnClickListener {
            skipToPreviousSong()
        }

        // SeekBar listener to handle seeking to a specific position
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?, progress: Int, fromUser: Boolean
            ) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Not needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Not needed
            }
        })
    }

    private fun updateSeekBar() {
        if (mediaPlayer.isPlaying) {
            seekBar.max = mediaPlayer.duration
            seekBar.progress = mediaPlayer.currentPosition
            handler.postDelayed({ updateSeekBar() }, 1000)
        }
    }

// ...


    private fun skipToNextSong() {
        if (playlist != null) {
            currentPosition = (currentPosition + 1) % playlist!!.size
            playCurrentSong()
            updateUIForCurrentSong()
        }
    }

    private fun skipToPreviousSong() {
        if (playlist != null) {
            currentPosition = if (currentPosition == 0) {
                playlist!!.size - 1
            } else {
                currentPosition - 1
            }
            playCurrentSong()
            updateUIForCurrentSong()
        }
    }


    private fun playCurrentSong() {
        if (playlist != null && currentPosition >= 0 && currentPosition < playlist!!.size) {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(playlist!![currentPosition])

            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener { mp ->
                mp.start()
                playButton.setImageResource(R.drawable.pause)
                isPlaying = true
                updateSeekBar()
                updateUIForCurrentSong()
            }
        }
    }


    private fun updateUIForCurrentSong() {
        if (playlist != null && currentPosition >= 0 && currentPosition < playlist!!.size) {
            val title = songTitles[currentPosition] ?: "Unknown Title"
            val artist = artists[currentPosition]
            val duration = songDuration // You can set the actual duration here

            binding.songTitle.text = title
            binding.songArtist.text = artist
            binding.songDuration.text = duration
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
            lyricsApiClient.fetchLyrics(title, artist)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        const val ARTIST = ""
        const val SONG_ID = "userID"
        const val SONG_TITLE = "title"
        const val SONG_DURATION = "duration"
        const val PLAYLIST = "playlist"
        const val CURRENT_POSITION = "current_position"
    }
}
