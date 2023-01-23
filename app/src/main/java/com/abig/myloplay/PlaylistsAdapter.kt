package com.abig.myloplay

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.OthersPlaylistItemBinding
import com.abig.myloplay.databinding.PlaylistItemBinding

class PlaylistsAdapter : RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder>() {

    private val playlists = mutableListOf<Playlist>()


    fun add(playlist: Playlist) {
        playlists.add(playlist)
        notifyDataSetChanged()
    }

    fun clear() {
        playlists.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.playlist_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
    }

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: PlaylistItemBinding = PlaylistItemBinding.bind(itemView)
        fun bind(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.playlistNumSongs.text =
                itemView.context.getString(R.string.num_songs, playlist.songIds.size)

            // Set up click listener for the playlist
            itemView.setOnClickListener {
                // Open the playlist details screen
                val intent = Intent(itemView.context, PlaylistDetailsActivity::class.java)
                intent.putExtra(PlaylistDetailsActivity.EXTRA_PLAYLIST_ID, playlist.id)
                itemView.context.startActivity(intent)
            }

        }
    }
}

