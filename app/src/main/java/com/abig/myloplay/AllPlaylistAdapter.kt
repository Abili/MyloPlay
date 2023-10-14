package com.abig.myloplay

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AllPlaylistAdapter : RecyclerView.Adapter<AllPlaylistAdapter.PlaylistViewHolder>() {
    private val playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.owner_pl_item, parent, false
        )
        return PlaylistViewHolder(view)
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

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playlistName: TextView = itemView.findViewById(R.id.textViewPlaylistName)
        private var auth: FirebaseAuth = FirebaseAuth.getInstance()


        init {
            itemView.setOnClickListener {
                // Handle playlist item click here
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedPlaylist = playlists[position]
                    // Implement logic to display songs for the selected playlist
                    val intent = Intent(itemView.context, AudioActivity::class.java)
                    intent.putExtra(AudioActivity.EXTRA_PLAYLIST_ID, selectedPlaylist.id)
                    intent.putExtra(AudioActivity.EXTRA_USER_ID, selectedPlaylist.userId)
                    itemView.context.startActivity(intent)
                }
            }
        }

        fun bind(playlist: Playlist) {
            playlistName.text = playlist.name
        }
    }
}
