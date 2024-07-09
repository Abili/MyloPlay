package com.abig.myloplay

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.OthersPlaylistItemBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random

class OthersAdapter(private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<OthersAdapter.PlaylistViewHolder>() {
    private val playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = OthersPlaylistItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
    }

    override fun getItemCount(): Int = playlists.size

    fun setPlaylists(playlists: List<Playlist>) {
        this.playlists.clear()
        this.playlists.addAll(playlists)
        notifyDataSetChanged()
    }

    inner class PlaylistViewHolder(private val binding: OthersPlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val playlistName: TextView = binding.textViewPlaylistName
        private val othersProfileImage: ImageView = binding.othersProfileImage
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()

        init {
            binding.root.setOnClickListener {
                // Handle playlist item click here
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedPlaylist = playlists[position]
                    // Implement logic to display songs for the selected playlist
                    val intent = Intent(binding.root.context, AudioActivity::class.java)
                    intent.putExtra(AudioActivity.EXTRA_PLAYLIST_TYPE, "single")
                    intent.putExtra(AudioActivity.EXTRA_PLAYLIST_SEEN, "seen")
                    intent.putExtra(AudioActivity.EXTRA_PLAYLIST_ID, selectedPlaylist.id)
                    intent.putExtra(AudioActivity.EXTRA_USER_ID, selectedPlaylist.userId)
                    intent.putExtra(AudioActivity.EXTRA_PLAYLIST_NAME, selectedPlaylist.name)
                    binding.root.context.startActivity(intent)
                }
            }
        }

        fun bind(playlist: Playlist) {
            retrieveOtherUserPlaylists(playlist.id!!, playlist.userId!!)
            playlistName.text = playlist.name
            binding.textViewUserName.text = playlist.userName

            binding.options.setOnClickListener {
                val optionsBottomSheetFragment = OptionsBottomSheetFragment()
                val args = Bundle()
                args.putString("playlistName", playlist.name)
                args.putString("userId", playlist.userId)
                args.putString("userId", playlist.userId)
                args.putString("playlistId", playlist.id)
                args.putString("playlistType", "single")

                optionsBottomSheetFragment.arguments = args
                optionsBottomSheetFragment.show(fragmentManager, optionsBottomSheetFragment.tag)

            }
        }

        private fun retrieveOtherUserPlaylists(playlistId: String, uid: String) {
            val database = FirebaseDatabase.getInstance().reference.child("users").child(uid)
            database.child("playlists").child("single").child("playlists").child(playlistId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val playlistName =
                            snapshot.child("playlistName").getValue(String::class.java)
                        val songsSnapshot = snapshot.child("songs")
                        val lastSongSnapshot = songsSnapshot.children.lastOrNull()
                        val lastSongAlbumArtUrl =
                            lastSongSnapshot?.child("albumArt")?.getValue(String::class.java)

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

                        // Load the album art into the ImageView using Glide
                        if (lastSongAlbumArtUrl != null) {
                            Glide.with(binding.root)
                                .load(lastSongAlbumArtUrl)
                                .centerCrop()
                                .into(binding.othersProfileImage)
                        } else {
                            // If no album art is found, set a random background color
                            val randomColor = getRandomColor()
                            val colorDrawable = getColorDrawable(randomColor)
                            Glide.with(binding.root)
                                .load(colorDrawable)
                                .centerCrop()
                                .into(binding.othersProfileImage)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle errors
                    }
                })
        }

        private fun getRandomColor(): Int {
            val colors = listOf("#2196F3", "#F44336", "#3F51B5", "#ED0404", "#E29C27B0", "#568651")
            val randomColor = Color.parseColor(colors[Random.nextInt(colors.size)])

            val alpha = (255 * 0.5).toInt()
            return Color.argb(
                alpha,
                Color.red(randomColor),
                Color.green(randomColor),
                Color.blue(randomColor)
            )

        }

        private fun getColorDrawable(color: Int): BitmapDrawable {
            val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(color)
            return BitmapDrawable(binding.root.resources, bitmap)
        }

    }
}
