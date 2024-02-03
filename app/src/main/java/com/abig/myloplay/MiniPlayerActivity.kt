package com.abig.myloplay

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.abig.myloplay.databinding.MiniPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MiniPlayerActivity : BottomSheetDialogFragment() {
    private lateinit var binding: MiniPlayerBinding
    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler()
    private var isPlaying = false
    private var playlist: List<String>? = null
    private var currentPosition: Int = 0
    private lateinit var songTitles: List<String>
    private lateinit var artists: List<String>
    private lateinit var songDuration: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MiniPlayerBinding.inflate(layoutInflater)

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

        //val intent = Intent()
        playlist = arguments?.getStringArrayList(PLAYLIST)
        currentPosition = arguments?.getInt(CURRENT_POSITION, 0)!!

        songTitles = arguments?.getStringArrayList(SONG_TITLE)!!
        artists = arguments?.getStringArrayList(ARTIST)!!
        songDuration = arguments?.getStringArrayList(SONG_DURATION)!!

        updateSeekBar()
        playCurrentSong()

        // Set up click listeners
        binding.playBtn.setOnClickListener { togglePlayPause() }
        binding.nextBtn.setOnClickListener { skipToNextSong() }
        binding.prevBtn.setOnClickListener { skipToPreviousSong() }
       // binding.miniPlayBtn.setOnClickListener { togglePlayPause() }

        // SeekBar listener to handle seeking to a specific position
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

//        binding.miniPlayerLayout.setOnClickListener {
//            binding.expandedplayerbottmsheet.visibility = View.VISIBLE
//            binding.miniPlayerLayout.visibility = View.GONE
//        }


        return binding.root
    }
    fun playShuffledSongs(shuffledPlaylist: List<String>, startPosition: Int) {
        playlist = shuffledPlaylist
        currentPosition = startPosition
        playCurrentSong()
        updateUIForCurrentSong()
    }

    private fun togglePlayPause() {
        if (isPlaying) {
            mediaPlayer.pause()
            binding.playBtn.setImageResource(R.drawable.play)
            //binding.miniPlayBtn.setImageResource(R.drawable.play)
        } else {
            mediaPlayer.start()
            binding.playBtn.setImageResource(R.drawable.pause)
            //binding.miniPlayBtn.setImageResource(R.drawable.pause)
            updateSeekBar()
        }
        isPlaying = !isPlaying
    }

    private fun updateSeekBar() {
        if (mediaPlayer.isPlaying) {
            val totalDuration = mediaPlayer.duration
            val currentPosition = mediaPlayer.currentPosition

            binding.seekbar.max = totalDuration
            //binding.miniseekbar.max = totalDuration
            binding.seekbar.progress = currentPosition
            //binding.miniseekbar.progress = currentPosition

            // Update the endSongDuration with the total duration
            binding.endsongDuration.text = formatDuration(totalDuration)

            // Update the startSongDuration with the current position
            binding.startsongDuration.text = formatDuration(currentPosition)

            // Update the startSongDuration with the current position
            //binding.miniDuration.text = formatDuration(currentPosition)

            handler.postDelayed({ updateSeekBar() }, 1000)
        }
    }

    private fun skipToNextSong() {
        if (playlist != null) {
            currentPosition = (currentPosition + 1) % playlist!!.size
            playCurrentSong()
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
        }
    }

    private fun playCurrentSong() {
        if (playlist != null && currentPosition >= 0 && currentPosition < playlist!!.size) {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(playlist!![currentPosition])

            mediaPlayer.setOnPreparedListener { mp ->
                mp.start()
                binding.playBtn.setImageResource(R.drawable.pause)
                //binding.miniPlayBtn.setImageResource(R.drawable.pause)
                isPlaying = true
                updateSeekBar()
                updateUIForCurrentSong()
            }

            mediaPlayer.prepareAsync()
        }
    }

    private fun updateUIForCurrentSong() {
        if (playlist != null && currentPosition >= 0 && currentPosition < playlist!!.size) {
            val title = songTitles[currentPosition] ?: "Unknown Title"
            val artist = artists[currentPosition]

            binding.songTitle.text = title
            binding.songArtist.text = artist

//            binding.miniSongTitle.text = title
//            binding.miniSongArtist.text = artist
        }
    }

    private fun formatDuration(duration: Int): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
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
