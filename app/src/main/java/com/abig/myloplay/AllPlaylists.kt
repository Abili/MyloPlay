package com.abig.myloplay

import android.Manifest
import android.content.Context
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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.ActivityAllPlaylistsBinding
import com.abig.myloplay.databinding.DialogAddPlaylistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class AllPlaylists : AppCompatActivity() {

    private lateinit var othersPlayListAdapter: OthersAdapter
    private lateinit var recommendationsAdapter: RecommendationsAdapter
    private lateinit var currentPlayListAdapter: AllPlaylistAdapter
    private lateinit var groupsAdapter: GroupsAdapter
    private val requestCodePermission = 123
    private val requestCodeAudioSelection = 456
    private lateinit var binding: ActivityAllPlaylistsBinding
    private val selectedAudioFiles = mutableListOf<AudioFile>()
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var contactsRetriever: ContactsRetriever
    private val requestCodePermissionContacts = 124
    private val requestCodePermissionAudio = 457
    private lateinit var popupWindow: PopupWindow
    private var isOptionsVisible = false
    private var allPlaylists: MutableList<Playlist> = mutableListOf()
    private lateinit var filteredPlaylists: MutableList<Playlist>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllPlaylistsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactsRetriever = ContactsRetriever(this)
        // Check and request permissions
        database = FirebaseDatabase.getInstance().reference
        checkAndRequestContactsPermission()

        auth = FirebaseAuth.getInstance()

        binding.currentUserPlaylistsRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        currentPlayListAdapter = AllPlaylistAdapter()
        othersPlayListAdapter = OthersAdapter()
        groupsAdapter = GroupsAdapter()

        retrieveRecommendedSongs()

        recommendationsAdapter = RecommendationsAdapter {
            removeRecommendedSong(it)
        }

        binding.currentUserPlaylistsRecycler.adapter = currentPlayListAdapter
        binding.otherUsersPlaylistsRecycler.adapter = othersPlayListAdapter
        binding.recommendationsRecy.adapter = recommendationsAdapter
        binding.groupPlaylistsRecycler.adapter = groupsAdapter

        binding.recommendationsRecy.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val displayMetrics = Resources.getSystem().displayMetrics
        val columnWidth = resources.getDimension(R.dimen.column_width).toInt()
        val numberOfColumns = displayMetrics.widthPixels / columnWidth

        val gridLayoutManager = GridLayoutManager(this, numberOfColumns)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)

        binding.otherUsersPlaylistsRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        binding.otherUsersPlaylistsRecycler.addItemDecoration(
//            GridSpacingItemDecoration(
//                numberOfColumns, spacingInPixels, false
//            )
//        )

        val groupGridLayoutManager = GridLayoutManager(this, numberOfColumns)
        binding.groupPlaylistsRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        binding.groupPlaylistsRecycler.addItemDecoration(
//            GridSpacingItemDecoration(
//                numberOfColumns, spacingInPixels, false
//            )
//        )

        binding.createPlaylist.setOnClickListener {
            toggleOptionsVisibility()
        }
        binding.singlePlaylist.setOnClickListener {
            openAudioSelection()
            AnimationUtils.loadAnimation(this, R.anim.slide_down)
        }

        binding.groupPlaylist.setOnClickListener {
            openAudioSelection()
            AnimationUtils.loadAnimation(this, R.anim.slide_down)
            startActivity(Intent(this, GroupPlaylistContacts::class.java))
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPlaylists(newText)
                return true
            }
        })


        retrieveCurrentUserPlaylists()
        //retrieveOtherUsersPlaylists()
    }

    private fun filterPlaylists(query: String?) {
        query?.let {
            filteredPlaylists = allPlaylists.filter { playlist ->
                val matchesQuery = playlist.name?.contains(
                    it, ignoreCase = true
                ) == true || // Search by playlist name
                        playlist.userName?.contains(
                            it, ignoreCase = true
                        ) == true // Search by user name

                // Filter songs within the playlist based on song names
                val filteredSongs = playlist.songs?.filter { song ->
                    song.title?.contains(it, ignoreCase = true) == true
                }

                matchesQuery || !filteredSongs.isNullOrEmpty()
            }.toMutableList()

            // Update your adapters with the filtered playlists
            currentPlayListAdapter.setPlaylists(filteredPlaylists)
            othersPlayListAdapter.setPlaylists(filteredPlaylists)
            groupsAdapter.setPlaylists(filteredPlaylists)
        }
    }


    private fun toggleOptionsVisibility() {
        isOptionsVisible = !isOptionsVisible

        val animation = if (isOptionsVisible) {
            AnimationUtils.loadAnimation(this, R.anim.slide_up)
        } else {
            AnimationUtils.loadAnimation(this, R.anim.slide_down)
        }

        binding.groupPlaylist.startAnimation(animation)
        binding.singlePlaylist.startAnimation(animation)

        binding.groupPlaylist.visibility = if (isOptionsVisible) View.VISIBLE else View.INVISIBLE
        binding.singlePlaylist.visibility = if (isOptionsVisible) View.VISIBLE else View.INVISIBLE
    }

    private fun checkAndRequestContactsPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS
            ) -> {
                // Permission already granted, retrieve contacts
                retrieveOtherUsersPlaylists()
                retrieveGroupPlaylists()
            }

            else -> {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CONTACTS_PERMISSION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CONTACTS_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, retrieve contacts
                retrieveOtherUsersPlaylists()
                retrieveGroupPlaylists()
            } else {
                // Permission denied, handle accordingly
                finish()
            }
        }
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

        if (requestCode == requestCodeAudioSelection && resultCode == RESULT_OK) {
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

    private fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }


    private fun getAudioFileFromUri(uri: Uri): AudioFile? {
        val cursor = contentResolver.query(uri, null, null, null, null)
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
            retriever.setDataSource(this, uri)
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
        mediaPlayer.setDataSource(applicationContext, uri)
        mediaPlayer.prepare()
        val duration = mediaPlayer.duration.toLong()
        mediaPlayer.release()
        return duration
    }


    private fun showPlaylistNameDialog(selectedSongs: List<AudioFile>) {
        val dialogBinding = DialogAddPlaylistBinding.inflate(layoutInflater)
        val dialog =
            AlertDialog.Builder(this).setView(dialogBinding.root).setTitle("Enter Playlist Name")
                .setPositiveButton("Save") { _, _ ->
                    val playlistName = dialogBinding.editTextPlaylistName.text.toString()

                    if (playlistName.isNotBlank()) {
                        savePlaylistToDatabase(selectedSongs, playlistName)
                        selectedAudioFiles.clear()

                    } else {
                        showToast("Playlist name cannot be empty.")
                    }
                }.setNegativeButton("Cancel") { _, _ ->
                    // Dismiss the dialog
                }.create()

        dialog.show()
    }

    private fun savePlaylistToDatabase(selectedSongs: List<AudioFile>, playlistName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Create a reference to the Firebase Realtime Database node for the user's playlists
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
            .child("playlists").child("single")

        // Create a new playlist entry in the Firebase Realtime Database
        val playlistRef = databaseRef.push()
        playlistRef.child("playlistName").setValue(playlistName)

        // Clear the previous songs data for this playlist
        val songsRef = playlistRef.child("songs")
        songsRef.removeValue() // This removes all data under "songs" for the current playlist

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
                                }.addOnFailureListener { _ ->
                                    showToast("Failed to retrieve the download URL for the album art.")
                                }
                            }.addOnFailureListener { _ ->
                                showToast("Failed to upload the album art to Firebase Storage.")
                            }
                        } else {
                            // No album art, save the song details without it
                            songEntryRef.setValue(songDetails)
                        }
                    }.addOnFailureListener { _ ->
                        showToast("Failed to retrieve the download URL for the song.")
                    }
            }.addOnFailureListener { _ ->
                showToast("Failed to upload the song to Firebase Storage.")
            }
        }

        showToast("Playlist '$playlistName' created successfully.")
    }


    private fun retrieveCurrentUserPlaylists() {
        // Retrieve playlists for the current user
        // ...
        val userId = auth.currentUser!!.uid
        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (usersnap in snapshot.children) {

                        val playlists = mutableListOf<Playlist>()
                        val username = usersnap.child("username").getValue(String::class.java)
                        val plsnapshot = snapshot.child(userId).child("playlists").child("single")

                        for (playlistShot in plsnapshot.children) {
                            val playlistId = playlistShot.key
                            val playlistname =
                                playlistShot.child("playlistName").getValue(String::class.java)
                            val playlist = Playlist(
                                playlistId!!,
                                playlistname!!,
                                userId,
                                null,
                                username,
                                System.currentTimeMillis(),
                                null
                            )
                            playlists.add(playlist)
                            //allPlaylists.add(playlist)
                            currentPlayListAdapter.setPlaylists(playlists)

                        }
                    }
                } else {
                    binding.myseperationTv.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //error
            }
        })


    }

    private fun retrieveOtherUsersPlaylists() {
        val currentUserContacts = mutableListOf<String>()

        // Fetch the current user's contacts from the phone
        val contacts = contactsRetriever.retrieveContacts()

        // Extract phone numbers from the retrieved contacts and format them
        currentUserContacts.addAll(contacts.map { formatPhoneNumber(it.phone) })

        // Retrieve playlists for other users
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        database.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playlists = mutableListOf<Playlist>()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userId = userSnapshot.key
                        val username = userSnapshot.child("username").getValue(String::class.java)
                        val phone = userSnapshot.child("phone").getValue(String::class.java)

                        if (userId != currentUserId) {
                            val isCurrentUserRestrictedByUser =
                                userSnapshot.child("restricted").child(currentUserId)
                                    .getValue(Boolean::class.java) ?: false

                            val isCurrentUserInContactList =
                                currentUserContacts.contains(formatPhoneNumber(phone!!))

                            if (!isCurrentUserRestrictedByUser && isCurrentUserInContactList) {
                                val playlistsSnapshot =
                                    userSnapshot.child("playlists").child("single")
                                for (playlistShot in playlistsSnapshot.children) {
                                    val playlistId = playlistShot.key
                                    val playListName = playlistShot.child("playlistName")
                                        .getValue(String::class.java)

                                    val playlist = Playlist(
                                        playlistId!!,
                                        playListName!!,
                                        userId!!,
                                        null,
                                        username,
                                        System.currentTimeMillis(),
                                        null
                                    )
                                    playlists.add(playlist)
                                   // allPlaylists.add(playlist)
                                }
                            }
                        }
                    }
                } else {
                    binding.seperationTv.visibility = View.GONE
                }
                othersPlayListAdapter.setPlaylists(playlists)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }

    private fun retrieveGroupPlaylists() {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        database.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playlists = mutableListOf<Playlist>()

                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key
                    val username = userSnapshot.child("username").getValue(String::class.java)

                    val playlistsSnapshot = userSnapshot.child("playlists").child("group")

                    for (playlistShot in playlistsSnapshot.children) {
                        val playlistId = playlistShot.key
                        val playlistName =
                            playlistShot.child("playlistName").getValue(String::class.java)

                        // Check if the current user's ID is either the creator or in the "userIds" array
                        val userIdsSnapshot = playlistShot.child("userIds")
                        if (playlistShot.child("userId").value == currentUserId || userIdsSnapshot.children.any { it.value == currentUserId }) {
                            // Log playlist information for debugging
                            Toast.makeText(
                                this@AllPlaylists, "PlaylistDebug $playlistName", Toast.LENGTH_SHORT
                            ).show()

                            val playlist = Playlist(
                                playlistId!!,
                                playlistName!!,
                                userId!!,
                                null,
                                username,
                                System.currentTimeMillis(),
                                null
                            )
                            playlists.add(playlist)
                            //allPlaylists.add(playlist)
                        }
                    }
                }

                groupsAdapter.setPlaylists(playlists)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }


    // Function to format the phone number
    private fun formatPhoneNumber(phone: String): String {
        return if (phone.startsWith("0")) {
            // Convert phone number starting with zero to +256 format
            "+256${phone.substring(1).replace(" ", "")}"
        } else {
            // Keep the existing format if it already starts with +256
            phone.replace(" ", "")
        }
    }

    private fun retrieveRecommendedSongs() {
        // Retrieve playlists for other users
        // ...
        database.child("users").child(auth.currentUser!!.uid).child("recommendations")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.recommendationsLayout.visibility = View.VISIBLE
                        val playlists = mutableListOf<Recommendation>()
                        for (recSnapshot in snapshot.children) {
                            //val userId = recSnapshot.key
                            val songId = recSnapshot.child("songId").getValue(String::class.java)
                            val songTitle =
                                recSnapshot.child("songTitle").getValue(String::class.java)
                            val artist = recSnapshot.child("artist").getValue(String::class.java)
                            val songUrl = recSnapshot.child("songUrl").getValue(String::class.java)
                            val duration =
                                recSnapshot.child("duration").getValue(String::class.java)
                            val recommendedBy =
                                recSnapshot.child("recommendedBy").getValue(String::class.java)
                            val recommendation = Recommendation(
                                songId,
                                songUrl!!,
                                songTitle!!,
                                artist!!,
                                duration!!,
                                recommendedBy!!
                            )
                            playlists.add(recommendation)


                        }
                        recommendationsAdapter.setRecommendedSong(playlists)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
    }

    private fun removeRecommendedSong(selectedSong: Recommendation) {
        // Remove the song from the list of songIds in the Firebase Realtime Database
        val playlistRef =
            FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid)
                .child("recommendations").child(selectedSong.songId!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val songIds =
                            snapshot.getValue<List<String>>()?.toMutableList() ?: mutableListOf()
                        // Remove the selected songId
                        songIds.remove(selectedSong.songId)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
    }


//    private fun retrieveAndStoreContacts() {
//        // Retrieve contacts
//        val contacts = contactsRetriever.retrieveContacts()
//        showToast("Number of contacts: ${contacts.size}")
//        // Store the contacts in Firebase
//        storeContactsInFirebase(contacts)
//    }

    // Other existing methods...

    private fun storeContactsInFirebase(contacts: List<User>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val contactsRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
            .child("contacts")

        // Clear the previous contacts data for this user
        contactsRef.removeValue()

        // Iterate through the retrieved contacts and store them in the Firebase Realtime Database
        contacts.forEach { contact ->
            val contactDetails = mapOf(
                "phone" to contact.phone,
                "username" to contact.username,
                // Add other contact details as needed
            )

            // Create a unique ID for the contact entry
            val contactEntryRef = contactsRef.push()

            // Save the contact details to the Firebase Realtime Database
            contactEntryRef.setValue(contactDetails)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val REQUEST_CONTACTS_PERMISSION = 1
    }
}
