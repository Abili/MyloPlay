package com.abig.myloplay

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.ActivityMusicPoolBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.UUID
import kotlin.math.min

class PoolActivity : Fragment() {
    private lateinit var audioListAdapter: AudioListAdapter
    private val requestCodePermission = 123
    private val requestCodeAudioSelection = 456
    private lateinit var binding: ActivityMusicPoolBinding
    private lateinit var playlistName: String
    private lateinit var username: String
    private lateinit var musicPoolId: String
    private lateinit var playlistType: String
    private lateinit var seenOrNot: String
    private lateinit var userId: String
    private val selectedAudioFiles = mutableListOf<AudioFile>()
    private lateinit var preferences: SharedPreferences

    //private var maxFileSizeBytes by Delegates.notNull<Long>()
    private lateinit var playlistsRef: DatabaseReference
    lateinit var userPl: DatabaseReference
    lateinit var myloPlayer: MyloPlayer
    private lateinit var mediaPlayer: MediaPlayer
    private var isOptionsVisible = false
    private var isPlaying = false

    private var playlist: List<String>? = null
    private lateinit var songTitles: List<String>
    private lateinit var artists: List<String>
    private lateinit var songDuration: List<String>
    private var currentPosition: Int = 0
    private val handler = Handler()
    private lateinit var shuffledPlaylist: MutableList<AudioFile>
    private var currentPlaylistId: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityMusicPoolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize SharedPreferences
        myloPlayer = MyloPlayer()
        //playlist = List<AudioFile>()

        // Initialize the MediaPlayer with audio attributes
        val audioAttributes =
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA).build()

        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(audioAttributes)

        mediaPlayer.setOnPreparedListener {
            // Media player is prepared, you can set the data source here.
            updateSeekBar()
            //playCurrentSong(it, currentPosition)
        }

        mediaPlayer.setOnCompletionListener {
            skipToNextSong(currentPosition, playlist!!)
        }


        preferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

// Retrieve the maxFileSizeBytes or use a default value
        //maxFileSizeBytes = preferences.getLong("maxFileSizeBytes", DEFAULT_MAX_FILE_SIZE.toLong())
        playlistsRef = FirebaseDatabase.getInstance().getReference("musicpool")
        userPl = FirebaseDatabase.getInstance().reference.child("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("musicpool")

        binding.poolRecycler.layoutManager = LinearLayoutManager(requireContext())
        audioListAdapter = AudioListAdapter(onRemoveClickListener = { selectedSong ->
            showRemoveConfirmationDialog(selectedSong)
        }, onRecommendClickListener = { selectedSong ->
            showRecommendationDialog(selectedSong)
        }) { playlists, position ->

            binding.apply {
                currentPosition = position

                // SeekBar listener to handle seeking to a specific position
                smallPlayer.seekbar.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
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


                for (song in playlists) {
                    playlist = listOf(song.downloadUrl!!)

                    songTitles = listOf(song.title.toString())
                    songDuration = listOf(song.duration.toString())
                    artists = listOf(song.artist.toString())
                    smallPlayer.root.visibility = View.VISIBLE

                    //smallPlayer.songTitle.text = songTitles.toString()
                    smallPlayer.songArtist.text = song.artist.toString()

                    playCurrentSong(playlist!!, position)

                    smallPlayer.playBtn.setOnClickListener {
                        togglePlayPause()
                    }

                    smallPlayer.nextBtn.setOnClickListener {
                        skipToNextSong(position, playlist!!)
                    }
                    smallPlayer.prevBtn.setOnClickListener {
                        skipToPreviousSong(position)
                    }

                }
            }
        }
        binding.poolRecycler.adapter = audioListAdapter


//add song
        binding.addSong.setOnClickListener {
            openAudioSelection()
        }
        retrieveSongsForAllUsers()
    }


    private fun stopMediaPlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
    }

    private fun playCurrentSong(playlist: List<String>, currentPosition: Int) {
        if (currentPosition >= 0 && currentPosition < playlist.size) {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(playlist[currentPosition].toString())

            mediaPlayer.setOnPreparedListener { mp ->
                mp.start()
                binding.smallPlayer.playBtn.setImageResource(R.drawable.pause)
                //binding.miniPlayBtn.setImageResource(R.drawable.pause)
                isPlaying = true
                updateSeekBar()
                updateUIForCurrentSong(currentPosition, playlist)
            }

            mediaPlayer.prepareAsync()
        }
    }

    private fun updateUIForCurrentSong(currentPosition: Int, playlist: List<String>) {
        if (currentPosition >= 0 && currentPosition < playlist.size) {
            val title = songTitles[currentPosition] ?: "Unknown Title"
            val artist = artists[currentPosition]

            binding.smallPlayer.songTitle.text = title
            binding.smallPlayer.songArtist.text = artist

//            binding.miniSongTitle.text = title
//            binding.miniSongArtist.text = artist
        }
    }

    private fun togglePlayPause() {
        if (isPlaying) {
            mediaPlayer.pause()
            binding.smallPlayer.playBtn.setImageResource(R.drawable.play)
            //binding.miniPlayBtn.setImageResource(R.drawable.play)
        } else {
            mediaPlayer.start()
            binding.smallPlayer.playBtn.setImageResource(R.drawable.pause)
            //binding.miniPlayBtn.setImageResource(R.drawable.pause)
            updateSeekBar()
        }
        isPlaying = !isPlaying
    }

    private fun updateSeekBar() {
        if (mediaPlayer.isPlaying) {
            val totalDuration = mediaPlayer.duration
            val currentPosition = mediaPlayer.currentPosition

            binding.smallPlayer.seekbar.max = totalDuration
            //binding.miniseekbar.max = totalDuration
            binding.smallPlayer.seekbar.progress = currentPosition
            //binding.miniseekbar.progress = currentPosition

            // Update the endSongDuration with the total duration
            binding.smallPlayer.endsongDuration.text = formatDuration(totalDuration)

            // Update the startSongDuration with the current position
            binding.smallPlayer.startsongDuration.text = formatDuration(currentPosition)

            // Update the startSongDuration with the current position
            //binding.miniDuration.text = formatDuration(currentPosition)

            handler.postDelayed({ updateSeekBar() }, 1000)
        }
    }

    private fun formatDuration(duration: Int): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun skipToNextSong(currentPosition: Int, playlist: List<String>) {
        this.currentPosition = (this.currentPosition + 1) % playlist.size
        playCurrentSong(playlist, currentPosition)
    }

    private fun skipToPreviousSong(currentPosition: Int) {
        if (playlist != null) {
            this@PoolActivity.currentPosition = if (currentPosition == 0) {
                playlist!!.size - 1
            } else {
                currentPosition - 1
            }
            playCurrentSong(playlist!!, currentPosition)
        }
    }

    private fun showRecommendationDialog(song: AudioFile) {
        val intent = Intent(requireActivity(), UserContacts::class.java)
        intent.putExtra("songUrl", song.downloadUrl)
        intent.putExtra("songTitle", song.title)
        intent.putExtra("artist", song.artist)
        intent.putExtra("duration", song.duration)
        intent.putExtra("userId", song.duration)
        startActivity(intent)
    }

    private fun showRemoveConfirmationDialog(selectedSong: AudioFile) {
        // User confirmed, remove the song from the playlist
        removeSongFromPlaylist(selectedSong)

    }

    private fun removeSongFromPlaylist(selectedSong: AudioFile) {
        // Remove the song from the list of songIds in the Firebase Realtime Database
        val playlistRef = playlistsRef.child(musicPoolId)
        userPl.child(musicPoolId).child("songs")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val songIds =
                        snapshot.getValue<List<String>>()?.toMutableList() ?: mutableListOf()

                    // Remove the selected songId
                    songIds.remove(selectedSong.id)

                    // Update the songIds in the Firebase Realtime Database for the current playlist
//                playlistRef.child("songIds").setValue(songIds).addOnSuccessListener {
//                    showToast("Song removed from the playlist.")
//                }.addOnFailureListener {
//                    showToast("Failed to remove the song from the playlist.")
//                }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun openAudioSelection() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "audio/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, requestCodeAudioSelection)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestCodeAudioSelection && resultCode == AppCompatActivity.RESULT_OK) {
            data?.let { intentData ->
                val selectedUris = intentData.clipData
                val singleUri = intentData.data

                if (selectedUris != null) {
                    for (i in 0 until selectedUris.itemCount) {
                        val uri = selectedUris.getItemAt(i).uri
                        val audioFile = getAudioFileFromUri(uri)

                        // Check if the song already exists in the list
                        if (audioFile != null && !selectedAudioFiles.contains(audioFile)) {
                            selectedAudioFiles.add(audioFile)
                        } else {
                            // Display a message indicating that the song already exists
                            showToast("Song '${audioFile?.title}' already exists.")
                        }
                    }
                    showPlaylistNameDialog(selectedAudioFiles) // Show the dialog

                } else if (singleUri != null) {
                    val audioFile = getAudioFileFromUri(singleUri)

                    // Check if the song already exists in the list
                    if (audioFile != null && !selectedAudioFiles.contains(audioFile)) {
                        selectedAudioFiles.add(audioFile)
                    } else {
                        // Display a message indicating that the song already exists
                        showToast("Song already exists.")
                    }

                    showPlaylistNameDialog(selectedAudioFiles) // Show the dialog
                    //selectedAudioFiles.clear()
                }
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private fun getAudioFileFromUri(uri: Uri): AudioFile? {
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val id = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DOCUMENT_ID)
                val title = it.getString(titleColumn)
                val duration = getDurationFromMediaStore(uri)
                val artist = getArtistFromUri(uri)

                return AudioFile(
                    id.toString(), uri.toString(), title, artist, Util.formatDuration(duration)
                )
            }
        }
        return null
    }

    private fun getArtistFromUri(uri: Uri): String {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(requireContext(), uri)
        val artist =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        mediaMetadataRetriever.release()
        return artist ?: "Unknown"
    }


    private fun getDurationFromMediaStore(uri: Uri): Long {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(requireContext(), uri)
        mediaPlayer.prepare()
        val duration = mediaPlayer.duration.toLong()
        mediaPlayer.release()
        return duration
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodePermission && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openAudioSelection()
        }
    }

    private fun showPlaylistNameDialog(selectedSongs: List<AudioFile>) {
        //val dialogBinding = DialogAddPlaylistBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext()).setTitle("Are you sure?")
            .setPositiveButton("confirm") { _, _ ->
                savePlaylistToDatabase(selectedSongs)
                selectedAudioFiles.clear()

            }.setNegativeButton("Cancel") { _, _ ->
                // Dismiss the dialog
            }.create()

        dialog.show()
    }

    private fun savePlaylistToDatabase(selectedSongs: List<AudioFile>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Create a reference to the Firebase Realtime Database node for the user's playlists
        val musicPoolRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
            .child("musicpool")

        //val databaseRef = musicPoolRef.child("single").child("playlists")


        // Create a new playlist entry in the Firebase Realtime Database
        val poolRefId = musicPoolRef.push()
        val key = poolRefId.key
        //databaseRef.child(playlistId).child("playlistName").setValue(playlistName)

        // Clear the previous songs data for this playlist
        val songsRef = musicPoolRef.child("songs").child(key!!)
        songsRef.removeValue() // This removes all data under "songs" for the current playlist

        // Iterate through the selected songs and store them in the Firebase Realtime Database
        selectedSongs.forEach { audioFile ->
            val songDetails = mutableMapOf(
                "displayName" to audioFile.title, "duration" to audioFile.duration
            )

            // Create a unique ID for the song entry
            //val songEntryRef = songsRef.push()

            // Generate a unique filename for the song in Firebase Storage
            val fileName = UUID.randomUUID().toString() + ".mp3"

            // Create a reference to the Firebase Storage location for the song file
            val storageRef = FirebaseStorage.getInstance().reference
            val songRef = storageRef.child("songs").child(audioFile.title!!)

            // Upload the song file to Firebase Storage
            val fileUri = Uri.parse(audioFile.downloadUrl)
            val uploadTask = songRef.putFile(fileUri)

            uploadTask.addOnSuccessListener { _ ->
                // Get the download URL of the uploaded song
                songRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Store the download URL and other song details in the Firebase Realtime Database
                    songDetails += ("downloadUrl" to downloadUrl.toString())
                    songsRef.setValue(songDetails)

                }.addOnFailureListener { _ ->
                    showToast("Failed to retrieve the download URL for the song.")
                }
            }.addOnFailureListener { _ ->
                showToast("Failed to upload the song to Firebase Storage.")
            }
        }

        showToast("Song added successfully.")
    }

    private fun retrieveSongsForAllUsers() {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                val allSongs = mutableListOf<AudioFile>()

                // Iterate through each user
                for (userSnapshot in usersSnapshot.children) {
                    val userId = userSnapshot.key // Get the user ID

                    // Check if the user has a musicpool node
                    val musicPoolSnapshot = userSnapshot.child("musicpool")
                    if (musicPoolSnapshot.exists()) {
                        val songsRef = musicPoolSnapshot.child("songs")

                        // Iterate through each song under the user's musicpool
                        for (songSnapshot in songsRef.children) {
                            val displayName = songSnapshot.child("displayName").value.toString()
                            val artist = songSnapshot.child("artist").value.toString()
                            val artistName = extractArtistName(displayName)
                            val duration = songSnapshot.child("duration").value.toString()
                            val downloadUrl = songSnapshot.child("downloadUrl").value.toString()

                            val song = AudioFile(userId!!, downloadUrl, displayName, artistName, duration)
                            allSongs.add(song)
                        }
                    }
                }

                // Set the retrieved songs to the adapter
                audioListAdapter.setData(allSongs)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
            }
        })
    }



    private fun retrieveUserName(uid: String, callback: (String) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

        databaseRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersnap: DataSnapshot) {
                // Check if the "username" key exists directly under the user's node
                val username = usersnap.child("username").getValue(String::class.java)

                // Use the callback to pass the username
                callback(username ?: "N/A")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
            }
        })
    }

    fun extractArtistName(displayName: String): String {
        val hyphenIndex = displayName.indexOf("-")
        val underscoreIndex = displayName.indexOf("_")

        val separatorIndex = when {
            hyphenIndex != -1 && underscoreIndex != -1 -> min(hyphenIndex, underscoreIndex)
            hyphenIndex != -1 -> hyphenIndex
            underscoreIndex != -1 -> underscoreIndex
            else -> -1
        }

        return if (separatorIndex != -1) {
            displayName.substring(0, separatorIndex).trim()
        } else {
            // If no separator found, return the whole display name or handle it as you prefer
            displayName.trim()
        }
    }

    fun downloadSong(downloadUrl: String, songName: String?) {
        // Retrieve the max file size preference
        // val maxFileSizeBytes = preferences.getLong("maxFileSizeBytes", DEFAULT_MAX_FILE_SIZE.toLong())

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Downloading...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.isIndeterminate = false
        progressDialog.max = 100
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Define the local directory where the songs will be saved
        val downloadDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "myloplay"
        )

        if (!downloadDirectory.exists()) {
            // If the directory doesn't exist, create it
            downloadDirectory.mkdirs()
        }

        // Define the local file where the song will be saved
        val defaultName = "UnknownSong.mp3" // Default name if title metadata is not available
        val localFile = File(downloadDirectory, songName ?: defaultName)

        try {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl)

            storageReference.metadata.addOnSuccessListener {
                val downloadTask = storageReference.getFile(localFile)

                downloadTask.addOnSuccessListener {
                    Toast.makeText(requireContext(), "Download completed", Toast.LENGTH_SHORT)
                        .show()
                    progressDialog.dismiss()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Download failed: $it", Toast.LENGTH_SHORT)
                        .show()
                }.addOnProgressListener { taskSnapshot ->
                    val progress =
                        (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                    val limitedProgress = progress.coerceAtMost(100)
                    progressDialog.progress = limitedProgress

                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error getting file metadata", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "An error occurred: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }


    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
        mediaPlayer.reset()
    }

    companion object {
        const val EXTRA_PLAYLIST_SEEN = "seen"
        const val EXTRA_USER_NAME = "userName"
        const val EXTRA_PLAYLIST_NAME = "playlistName"
        const val EXTRA_ALBUMART = "allbumArt"
        const val EXTRA_PLAYLIST_ID = "playlistId"
        const val EXTRA_PLAYLIST_TYPE = "group"
        const val EXTRA_USER_ID = "userID"
        const val PERMISSION_REQUEST_CODE = 0
        const val DEFAULT_MAX_FILE_SIZE = 1 * 1024 * 1024
    }

}
