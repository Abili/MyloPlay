package com.abig.myloplay

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.abig.myloplay.databinding.BottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class OptionsBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetLayoutBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetLayoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set playlist name
        val playlistName = arguments?.getString("playlistName")
        val userId = arguments?.getString("userId")
        val playlistId = arguments?.getString("playlistId")
        val playlistType = arguments?.getString("playlistType")

        binding.playlistNameTextView.text = playlistName
        val cid = FirebaseAuth.getInstance().currentUser!!.uid

        if (userId != FirebaseAuth.getInstance().currentUser!!.uid) {
            binding.deleteButton.visibility = View.GONE
        }

        // Delete button click listener
        binding.deleteButton.setOnClickListener {
            // Implement delete functionality
//            if (playlistType == "single") {
            val removeSingle = FirebaseDatabase.getInstance().reference.child("users").child(cid)
                .child("playlists").child(playlistType!!).child("playlists").child(playlistId!!)
            removeSingle.removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        requireActivity(), "Playlist Deleted Successfullly", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireActivity(), "Failed to Delete Playlist", Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dismiss()
//            } else {
//                val removeDouble =
//                    FirebaseDatabase.getInstance().reference.child("users").child(cid)
//                        .child("playlists").child("group").child("playlists").child(playlistId!!)
//                removeDouble.removeValue().addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        Toast.makeText(
//                            requireContext(), "Playlist Deleted Successfullly", Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                        Toast.makeText(
//                            requireContext(), "Failed to Delete Playlist", Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//                dismiss()
//            }
        }

        // dowload entire playlist button click listener
        binding.download.setOnClickListener {
            val myloplayDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "myloplay"
            )
            if (!myloplayDir.exists()) {
                myloplayDir.mkdirs()
            }

            val removeDouble =
                FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
                    .child("playlists").child(playlistType!!).child("playlists").child(playlistId!!)
                    .child("songs")
            removeDouble.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val totalSongs = snapshot.childrenCount
                    var downloadedSongs = 0
                    for (songSnap in snapshot.children) {
                        val songName = songSnap.child("displayName").getValue(String::class.java)
                        val downloadUrl = songSnap.child("downloadUrl").getValue(String::class.java)

                        // Download song file
                        val songFile = File(myloplayDir, "$songName.mp3")

                        val storageRef =
                            FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl!!)
                        val downloadTask = storageRef.getFile(songFile)
                        downloadTask.addOnProgressListener { taskSnapshot ->
                            val progress =
                                (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                            activity?.runOnUiThread {
                                // Show progress dialog on main thread
                                showProgressDialog(songName, progress)
                            }
                        }.addOnSuccessListener {
                            // File downloaded successfully
                            downloadedSongs++
                            if (downloadedSongs == totalSongs.toInt()) {
                                dismissProgressDialog()
                                showDownloadLocationSnackbar(myloplayDir.absolutePath)
                                dismiss() // Close the bottom sheet after download completes
                            }
                        }.addOnFailureListener { exception ->
                            // Handle any errors
                            dismissProgressDialog()
                            Toast.makeText(
                                requireActivity(),
                                "Failed to Download Playlist: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                private var progressDialog: ProgressDialog? = null

                private fun showProgressDialog(songName: String?, progress: Int) {
                    if (progressDialog == null) {
                        progressDialog = ProgressDialog(requireContext())
                        progressDialog!!.max = 100
                        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                        progressDialog!!.setCancelable(false)
                    }
                    progressDialog!!.setMessage("Downloading $songName... $progress%")
                    progressDialog!!.show()
                }

                private fun dismissProgressDialog() {
                    progressDialog?.dismiss()
                    progressDialog = null
                }

                private fun showDownloadLocationSnackbar(downloadLocation: String) {
                    val snackbar = Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Playlist downloaded to $downloadLocation",
                        Snackbar.LENGTH_INDEFINITE
                    )
                    snackbar.setAction("Download Location") {
                        try {
                            // Open file manager to show download location using FileProvider
                            val uri = FileProvider.getUriForFile(
                                requireContext(),
                                "${requireActivity().packageName}.provider",
                                File(downloadLocation, "myloplay")
                            )
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(uri, "*/*")
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            requireActivity().startActivity(intent)
                        } catch (e: Exception) {
                            // Log the error
                            Log.e("", "Error opening download location: ${e.message}")
                        }
                    }
                    snackbar.show()
                }



                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled
                    dismissProgressDialog()
                }
            })
        }


    }

}