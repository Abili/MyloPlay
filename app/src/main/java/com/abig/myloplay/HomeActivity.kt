package com.abig.myloplay

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.ActivityHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity(), AddPlaylistDialogFragment.AddPlaylistDialogListener {

    // ViewBinding variable for the activity layout
    private lateinit var binding: ActivityHomeBinding
    private lateinit var readExternalStoragePermissionLauncher: ActivityResultLauncher<String>


    // FirebaseAuth instance
    private lateinit var auth: FirebaseAuth
    val imageUri = mutableStateOf<Uri?>(null)

    // FirebaseDatabase instance
    private lateinit var database: FirebaseDatabase
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null

    // RecyclerView adapter for displaying the playlists
    private lateinit var adapter: OwnersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth and FirebaseDatabase
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()


        openAudioFiles()
        //open the audio files

        // Set up the RecyclerView
        // ...
        // Initialize the adapters for the RecyclerViews
        val currentUserOwnersAdapter = OwnersAdapter()
        val otherUsersPlaylistsAdapter = OthersAdapter()

        // Set the adapters for the RecyclerViews
        binding.currentUserPlaylists.adapter = currentUserOwnersAdapter
        binding.otherUsersPlaylists.adapter = otherUsersPlaylistsAdapter

        //val gridLayoutManager = GridLayoutManager(this, 2)
        // Set the layout managers for the RecyclerViews
        binding.currentUserPlaylists.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        val displayMetrics = Resources.getSystem().displayMetrics
        val columnWidth = resources.getDimension(R.dimen.column_width).toInt()
        val numberOfColumns = displayMetrics.widthPixels / columnWidth

        val gridLayoutManager = GridLayoutManager(this, numberOfColumns)
        binding.otherUsersPlaylists.layoutManager = gridLayoutManager


        //recyclerView.layoutManager = gridLayoutManager
//        binding.otherUsersPlaylists
//            .addItemDecoration(
//                GridSpacingItemDecoration(
//                    1,
//                    10,
//                    true
//                )
//            )

        if (auth.currentUser == null) {
            binding.otherUsersPlaylists.visibility = View.GONE
        } else {
            binding.otherUsersPlaylists.visibility = View.VISIBLE
        }

        // Listen for changes to the playlists collection
        database.reference.child("playlists")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Clear the adapter and add the playlists from the snapshot
                    // Clear the adapters
                    currentUserOwnersAdapter.clear()
                    otherUsersPlaylistsAdapter.clear()

                    val playlists = dataSnapshot.children.mapNotNull { child ->
                        child.getValue(Playlist::class.java)
                    }
                    playlists.filter { it.userId == auth.currentUser?.uid }
                        .sortedByDescending { it.time }
                        .forEach { currentUserOwnersAdapter.add(it) }
                    playlists.filter { it.userId != auth.currentUser?.uid }
                        .forEach { otherUsersPlaylistsAdapter.add(it) }

                    // Notify the adapters that the data has changed
                    currentUserOwnersAdapter.notifyDataSetChanged()
                    otherUsersPlaylistsAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Listen failed.", error.toException())
                }
            })


        // Set up the floating action button
        binding.floatingActionButton.setOnClickListener {
            // Show the add playlist dialog

            //val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            // Set the type to audio files
//            intent.type = "audio/*"
//            // Allow the user to select multiple files
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            // Start the intent
//            startActivityForResult(intent, REQUEST_CODE_SELECT_SONGS)

            val intent =
                Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            activityResultLauncher!!.launch(intent)

//            val dialog = AddPlaylistDialogFragment()
//            dialog.show(supportFragmentManager, "addPlaylistDialog")
        }


    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("give permission")
                    .setMessage("Allow Access External Storage")
                    .setPositiveButton(
                        "OK"
                    ) { dialogInterface, i ->
                        ActivityCompat.requestPermissions(
                            this@HomeActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            1
                        )
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        openAudioFiles()
                    }
                } else {
                    Snackbar.make(
                        binding.root,
                        "Please provide the permission",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openAudioFiles() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    imageUri.value = Uri.parse(result.data.toString())
                    val selectedSongs = getSelectedAudioFiles(result.data)
                    // Get the song ids
                    // Create and show the add playlist dialog
                    val addPlaylistDialogFragment =
                        AddPlaylistDialogFragment.newInstance(this, selectedSongs)
                    addPlaylistDialogFragment.show(supportFragmentManager, "add_playlist_dialog")
                }
            }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_SONGS = 3
        private const val TAG = "HomeActivity"
        private const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_SONGS && resultCode == RESULT_OK) {
            // Get the selected audio files
            val selectedSongs = getSelectedAudioFiles(data)
            // Get the song ids

            // Create and show the add playlist dialog
            val addPlaylistDialogFragment =
                AddPlaylistDialogFragment.newInstance(this, selectedSongs)
            addPlaylistDialogFragment.show(supportFragmentManager, "add_playlist_dialog")
        }

    }

    private fun getSelectedAudioFiles(data: Intent?): List<Uri> {
        val selectedSongs = mutableListOf<Uri>()
        // Check if the Intent contains a clip data
        if (data?.clipData != null) {
            // Iterate over the clip data items
            for (i in 0 until data.clipData!!.itemCount) {
                val item = data.clipData!!.getItemAt(i)
                // Get the URI of the audio file
                val uri = item.uri
                selectedSongs.add(uri)
            }
        } else if (data?.data != null) {
            // Get the URI of the audio file
            val uri = data.data
            selectedSongs.add(uri!!)
        }
        return selectedSongs
    }


    override fun onPlaylistCreated(playlist: Playlist) {
        Snackbar.make(
            binding.otherUsersPlaylists,
            "PlaylistCreated successfully",
            Snackbar.LENGTH_SHORT
        )
            .show()
    }

}
