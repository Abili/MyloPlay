package com.abig.myloplay

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.OwnerPlItemBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MusicPoolAdapter(private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<MusicPoolAdapter.PlaylistViewHolder>() {
    private val genres = mutableListOf<Genre>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = OwnerPlItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = genres[position]
        holder.bind(playlist)
    }

    override fun getItemCount(): Int = genres.size

    fun setPlaylists(genres: MutableList<Genre>) {
        this.genres.clear()
        this.genres.addAll(genres)
        notifyDataSetChanged()
    }

    inner class PlaylistViewHolder(private val binding: OwnerPlItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var auth: FirebaseAuth = FirebaseAuth.getInstance()
        //lateinit var lastSongAlbumArtUrl: String

        init {
            binding.root.setOnClickListener {
                // Handle playlist item click here
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedPlaylist = genres[position]
                    // Implement logic to display songs for the selected playlist
                    val intent = Intent(binding.root.context, AudioActivity::class.java)
                    intent.putExtra(AudioActivity.EXTRA_PLAYLIST_TYPE, "single")
                    intent.putExtra(AudioActivity.EXTRA_PLAYLIST_ID, selectedPlaylist.id)
                    intent.putExtra(AudioActivity.EXTRA_USER_ID, selectedPlaylist.userId)
                    //intent.putExtra(AudioActivity.EXTRA_ALBUMART, lastSongAlbumArtUrl)
                    intent.putExtra(AudioActivity.EXTRA_USER_NAME, selectedPlaylist.userName)
                    intent.putExtra(AudioActivity.EXTRA_PLAYLIST_NAME, selectedPlaylist.name)
                    binding.root.context.startActivity(intent)
                }
            }
        }

        fun bind(genre: Genre) {
            binding.textViewPlaylistName.text = genre.name
            retrieveGenre(genre.id!!, genre.userId!!)

            binding.options.setOnClickListener {
                val optionsBottomSheetFragment = OptionsBottomSheetFragment()
                val args = Bundle()
                args.putString("playlistName", genre.name)
                args.putString("userId", genre.userId)
                args.putString("playlistId", genre.id)
                args.putString("playlistType", "single")

                optionsBottomSheetFragment.arguments = args
                optionsBottomSheetFragment.show(fragmentManager, optionsBottomSheetFragment.tag)

            }
        }

        private fun retrieveGenre(genreId: String, userId: String) {
            val database = FirebaseDatabase.getInstance().reference.child("users").child(userId)
                .child("genre").child(genreId)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val playlistName = snapshot.child("genreName").getValue(String::class.java)
                    val songsSnapshot = snapshot.child("songs")
                    val lastSongSnapshot = songsSnapshot.children.lastOrNull()
                    val songsCount = songsSnapshot.childrenCount
                    if (songsCount.toInt() == 1) {
                        binding.textViewNumSongs.text = buildString {
                            append(songsCount)
                            append(" song")
                        }
                    } else {
                        binding.textViewNumSongs.text = buildString {
                            append(songsCount)
                            append(" songs")
                        }
                    }

                    val lastSongAlbumArtUrl =
                        lastSongSnapshot?.child("albumArt")?.getValue(String::class.java)
                    if (lastSongAlbumArtUrl != null) {
                        // Load the album art into the ImageView using Glide
                        Glide.with(binding.root).load(lastSongAlbumArtUrl).centerCrop()
                            .into(binding.ownersProfileImage)
                    } else {
                        Glide.with(binding.root).load(R.drawable.mylo_bg_logo).centerCrop()
                            .into(binding.ownersProfileImage)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
        }
    }
}
