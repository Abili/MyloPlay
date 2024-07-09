package com.abig.myloplay

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.ActivityGenreBinding
import com.abig.myloplay.databinding.ActivityMusicPoolBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class MusicPoolFragment : Fragment() {

    private lateinit var musicPoolAdapter: MusicPoolAdapter
    private lateinit var binding: ActivityMusicPoolBinding
    private val selectedAudioFiles = mutableListOf<AudioFile>()
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var allPlaylists: MutableList<Genre> = mutableListOf()
    private lateinit var filteredPlaylists: MutableList<Genre>

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
        setupUI()
    }

    private fun setupUI() {
        // Initialization code moved from onCreate
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
//        playListViewModel = ViewModelProvider(
//            this, PlayListViewModel.Factory(this)
//        )[PlayListViewModel::class.java]

        // Initialize FirebaseAuth
        //auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(requireActivity(), SignUp::class.java))
            requireActivity().finish()
        }



        binding.poolRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        musicPoolAdapter = MusicPoolAdapter(requireFragmentManager())


        binding.poolRecycler.adapter = musicPoolAdapter


        val displayMetrics = Resources.getSystem().displayMetrics
        val columnWidth = resources.getDimension(R.dimen.column_width).toInt()
        val numberOfColumns = displayMetrics.widthPixels / columnWidth

        val gridLayoutManager = GridLayoutManager(requireContext(), numberOfColumns)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)


        binding.creatmyplaylistBtn.setOnClickListener {
            openAudioSelection()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterSongs(newText)
                return true
            }
        })


        retrieveMusicPool()
        checkAndRequestContactsPermission()
    }

    private fun filterSongs(query: String?) {
        query?.let {
            filteredPlaylists = allPlaylists.filter { pool ->
                val matchesQuery = pool.name?.contains(
                    it, ignoreCase = true
                ) == true || // Search by pool name
                        pool.userName?.contains(
                            it, ignoreCase = true
                        ) == true // Search by user name

                // Filter songs within the pool based on song names
                val filteredSongs = pool.songs?.filter { song ->
                    song.title?.contains(it, ignoreCase = true) == true
                }

                matchesQuery || !filteredSongs.isNullOrEmpty()
            }.toMutableList()

            // Update your adapters with the filtered playlists
            musicPoolAdapter.setPlaylists(filteredPlaylists)
        }
    }


    private fun checkAndRequestContactsPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_CONTACTS
            ) -> {
                // Permission already granted, retrieve contacts
            }

            else -> {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    REQUEST_CONTACTS_PERMISSION
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CONTACTS_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            } else {
                // Permission denied, handle accordingly
                requireActivity().finish()
            }
        }
    }


    private val audioSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                val audioFile = getAudioFileFromUri(uri)
                audioFile?.let {
                    selectedAudioFiles.add(audioFile)
                    showPlaylistNameDialog(selectedAudioFiles)
                }
            }
        }

    private fun openAudioSelection() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "audio/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        audioSelectionLauncher.launch(arrayOf((intent).toString()))
    }


    private fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), message, duration).show()
    }


    private fun getAudioFileFromUri(uri: Uri): AudioFile? {
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val id = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DOCUMENT_ID)

                // Check if the "artist" column exists before retrieving its value
                val artistColumnIndex = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val artist = if (artistColumnIndex != -1) {
                    it.getString(artistColumnIndex)
                } else {
                    "Unknown"
                }

                val title = it.getString(titleColumn)
                val duration = getDurationFromMediaStore(uri)
                val albumArt = getAlbumArtFromMediaStore(uri)

                return AudioFile(
                    id.toString(),
                    uri.toString(),
                    title,
                    artist,
                    Util.formatDuration(duration),
                    albumArt
                )
            }
        }
        return null
    }

    private fun getAlbumArtFromMediaStore(uri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(requireContext(), uri)
            val albumArtByteArray = retriever.embeddedPicture
            if (albumArtByteArray != null) {
                // Convert the byte array to a Bitmap
                return BitmapFactory.decodeByteArray(albumArtByteArray, 0, albumArtByteArray.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            retriever.release()
        }
        return null
    }


    private fun getDurationFromMediaStore(uri: Uri): Long {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(requireContext(), uri)
        mediaPlayer.prepare()
        val duration = mediaPlayer.duration.toLong()
        mediaPlayer.release()
        return duration
    }


    private fun showPlaylistNameDialog(selectedSongs: List<AudioFile>) {
        val dialogBinding = ActivityGenreBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root)
            .setTitle("Select Genre").setPositiveButton("Save") { _, _ ->
                val selectedGenre = dialogBinding.genreSpinner.selectedItem as String

                if (selectedGenre.isNotBlank()) {
                    binding.creatingPlDialog.visibility = View.VISIBLE
                    savePlaylistToDatabase(selectedSongs, selectedGenre)
                    selectedAudioFiles.clear()

                } else {
                    showToast("Please select a genre.")
                }
            }.setNegativeButton("Cancel") { _, _ ->
                // Dismiss the dialog
            }.create()

        dialog.show()
    }


    private fun savePlaylistToDatabase(selectedSongs: List<AudioFile>, genre: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Create a reference to the Firebase Realtime Database node for the user's playlists
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
            .child("musicpool")

        // Create a new playlist entry in the Firebase Realtime Database
        val playlistRef = databaseRef.push()
        playlistRef.child("genre").setValue(genre)

        // Clear the previous songs data for this playlist
        val songsRef = playlistRef.child("songs")
        songsRef.removeValue() // this removes all data under "songs" for the current playlist

        // Iterate through the selected songs and store them in the Firebase Realtime Database
        selectedSongs.forEach { audioFile ->
            val songDetails = mutableMapOf(
                "displayName" to audioFile.title,
                "artist" to audioFile.artist,
                "duration" to audioFile.duration
            )

            // Create a unique ID for the song entry
            val songEntryRef = songsRef.push()

            // Generate a unique filename for the song in Firebase Storage
            val fileName = UUID.randomUUID().toString() + ".mp3"

            // Upload the song file to Firebase Storage
            val fileUri = Uri.parse(audioFile.downloadUrl)
            val uploadTask = FirebaseStorage.getInstance().reference.child("songs").child(fileName)
                .putFile(fileUri)

            uploadTask.addOnSuccessListener { _ ->
                // Get the download URL of the uploaded song
                FirebaseStorage.getInstance().reference.child("songs")
                    .child(fileName).downloadUrl.addOnSuccessListener { songDownloadUrl ->

                        // Store the download URL in the Firebase Realtime Database
                        songDetails += ("downloadUrl" to songDownloadUrl.toString())

                        // Now, upload the album art to Firebase Storage
                        val albumArtBitmap = audioFile.albumArt
                        if (albumArtBitmap != null) {
                            val albumArtFileName = UUID.randomUUID().toString() + ".jpg"
                            val albumArtRef =
                                FirebaseStorage.getInstance().reference.child("album_arts")
                                    .child(albumArtFileName)

                            val albumArtStream = ByteArrayOutputStream()
                            albumArtBitmap.compress(Bitmap.CompressFormat.JPEG, 100, albumArtStream)
                            val albumArtData = albumArtStream.toByteArray()

                            val albumArtUploadTask = albumArtRef.putBytes(albumArtData)

                            albumArtUploadTask.addOnSuccessListener { _ ->
                                // Get the download URL of the uploaded album art
                                albumArtRef.downloadUrl.addOnSuccessListener { albumArtDownloadUrl ->
                                    // Add the album art URL to the song details
                                    songDetails += ("albumArt" to albumArtDownloadUrl.toString())

                                    // Save the song details to the Firebase Realtime Database
                                    songEntryRef.setValue(songDetails)
                                    binding.creatingPlDialog.visibility = View.GONE
                                    showToast("Music '$genre' Added successfully.")

                                }.addOnFailureListener { _ ->
                                    showToast("Failed to retrieve the download URL for the album art.")
                                }
                            }.addOnFailureListener { _ ->
                                showToast("Failed to upload the album art to Firebase Storage.")
                            }
                        } else {
                            // No album art, save the song details without it
                            songEntryRef.setValue(songDetails)
                            binding.creatingPlDialog.visibility = View.GONE
                            showToast("Music '$genre' Added successfully.")
                        }
                    }.addOnFailureListener { _ ->
                        showToast("Failed to retrieve the download URL for the song.")
                    }
            }.addOnFailureListener { _ ->
                showToast("Failed to upload the song to Firebase Storage.")
            }
        }

    }

    private fun retrieveMusicPool() {
        // Retrieve playlists for the current user
        // ...
        val userId = auth.currentUser!!.uid
        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (usersnap in snapshot.children) {

                    val genres = mutableListOf<Genre>()
                    val username = usersnap.child("username").getValue(String::class.java)
                    val uid = usersnap.child("id").getValue(String::class.java)
                    val plsnapshot =
                        snapshot.child(userId).child("musicpool")
                    if (snapshot.exists()) {
                        for (playlistShot in plsnapshot.children) {
                            val playlistId = playlistShot.key
                            val genreName =
                                playlistShot.child("genre").getValue(String::class.java)
                            val genre = Genre(
                                playlistId!!,
                                genreName!!,
                                userId,
                                null,
                                username,
                                System.currentTimeMillis(),
                                null
                            )
                            genres.add(genre)
                            //allPlaylists.add(playlist)
                            musicPoolAdapter.setPlaylists(genres)

                        }
                    } else {
                        binding.myplTv.visibility = View.VISIBLE
                        binding.creatmyplaylistBtn.visibility = View.VISIBLE
                        binding.poolRecycler.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //error
            }
        })


    }

    companion object {
        private const val REQUEST_CONTACTS_PERMISSION = 1
    }
}
