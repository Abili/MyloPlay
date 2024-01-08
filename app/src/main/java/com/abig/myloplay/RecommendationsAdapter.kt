package com.abig.myloplay

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.RecommendationsItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RecommendationsAdapter(private val onRemoveClickListener: (Recommendation) -> Unit) :
    RecyclerView.Adapter<RecommendationsAdapter.PlaylistViewHolder>() {
    private val recommendations = mutableListOf<Recommendation>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding =
            RecommendationsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = recommendations[position]
        holder.bind(playlist)
    }

    override fun getItemCount(): Int = recommendations.size

    fun setRecommendedSong(playlists: List<Recommendation>) {
        this.recommendations.clear()
        this.recommendations.addAll(playlists)
        notifyDataSetChanged()
    }

    inner class PlaylistViewHolder(private val binding: RecommendationsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var auth: FirebaseAuth = FirebaseAuth.getInstance()
        //lateinit var lastSongAlbumArtUrl: String

        init {
            itemView.setOnClickListener {
                // Handle playlist item click here
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedSong = recommendations[position]
                    // Implement logic to display songs for the selected playlist
                    val intent = Intent(itemView.context, MyloPlayer::class.java)
                    intent.putStringArrayListExtra(
                        MyloPlayer.PLAYLIST,
                        ArrayList(recommendations.map { it.songUrl })
                    )
                    intent.putExtra(MyloPlayer.CURRENT_POSITION, position)
                    intent.putExtra(MyloPlayer.SONG_ID, selectedSong.songUrl)
                    intent.putStringArrayListExtra(
                        MyloPlayer.SONG_TITLE,
                        ArrayList(recommendations.map { it.songTitle })
                    )
                    intent.putStringArrayListExtra(
                        MyloPlayer.ARTIST,
                        ArrayList(recommendations.map { it.artist })
                    )
                    intent.putExtra(MyloPlayer.SONG_DURATION, selectedSong.duration)
                    itemView.context.startActivity(intent)
                }
            }

            binding.songMenu.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, binding.rootLayout)
                popupMenu.inflate(R.menu.song_menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.song_download -> {
                            // Handle the download action
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                val selectedSong = recommendations[position]
                                // Pass the download URL to the AudioActivity
                                (view.context as? AudioActivity)?.downloadSong(
                                    selectedSong.songUrl,
                                    selectedSong.songTitle
                                )
                            }
                            true // Return true to indicate that the item is handled
                        }

//                        R.id.song_recommend -> {
//                            val position = adapterPosition
//                            if (position != RecyclerView.NO_POSITION) {
//                                val selectedSong = recommendations[position]
//                                // Call the recommend click listener
//                                onRecommendClickListener.invoke(selectedSong)
//                            }
//                            true
//                        }

                        R.id.deleteSong -> {
                            // Handle the recommend action
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                val selectedSong = recommendations[position]
                                showRemoveConfirmationDialog(selectedSong)
                            }
                            true
                        }

                        else -> false // Return false for unhandled items
                    }
                }
                popupMenu.show()
            }
        }

        private fun showRemoveConfirmationDialog(song: Recommendation) {
            val context = binding.root.context
            AlertDialog.Builder(context)
                .setTitle("Remove Song")
                .setMessage("Are you sure you want to remove this song from the playlist?")
                .setPositiveButton("Remove") { _, _ ->
                    // User confirmed, invoke the remove click listener
                    onRemoveClickListener.invoke(song)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        fun bind(recommendation: Recommendation) {
            binding.recommender.text = recommendation.recommendedBy
            binding.textTitle.text = recommendation.songTitle
            binding.textArtist.text = recommendation.artist
            binding.textDuration.text = recommendation.duration.toString()
            //retrieveCurrentUserPlaylists(recommendation.id!!, recommendation.userId!!)

            FirebaseDatabase.getInstance().reference.child("users")
                .child(recommendation.recommendedBy)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username = snapshot.child("username").getValue(String::class.java)
                        binding.recommender.text = buildString {
                            append("Recommended by ")
                            append(username)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //handle error
                    }

                })
        }


//        private fun retrieveCurrentUserPlaylists(playlistId: String, userId: String) {
//            val database = FirebaseDatabase.getInstance().reference.child("users").child(userId)
//                .child("playlists").child(playlistId)
//            database.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val playlistName = snapshot.child("playlistName").getValue(String::class.java)
//                    val songsSnapshot = snapshot.child("songs")
//                    val lastSongSnapshot = songsSnapshot.children.lastOrNull()
//                    val songsCount = songsSnapshot.childrenCount
//                    if (songsCount.toInt() == 1) {
//                        binding.textViewNumSongs.text = buildString {
//                            append(songsCount)
//                            append(" song")
//                        }
//                    } else {
//                        binding.textViewNumSongs.text = buildString {
//                            append(songsCount)
//                            append(" songs")
//                        }
//                    }
//
//                    val lastSongAlbumArtUrl =
//                        lastSongSnapshot?.child("albumArt")?.getValue(String::class.java)
//                    if (lastSongAlbumArtUrl != null) {
//                        // Load the album art into the ImageView using Glide
//                        Glide.with(binding.root).load(lastSongAlbumArtUrl).centerCrop()
//                            .into(binding.ownersProfileImage)
//                    } else {
//                        Glide.with(binding.root).load(R.drawable.mylo_bg_logo).centerCrop()
//                            .into(binding.ownersProfileImage)
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // Handle errors
//                }
//            })
//        }
    }
}
