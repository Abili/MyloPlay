package com.abig.myloplay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.ItemSongBinding

class AudioListAdapter :
    RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {
    private val songs = mutableListOf<AudioFile>()

    fun setData(song: MutableList<AudioFile>) {
        songs.clear()
        songs.addAll(song)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    inner class ViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: AudioFile) {
            binding.textTitle.text = song.title
            binding.textDuration.text = song.duration.toString()
            binding.textArtist.text = song.artist.toString()
        }
    }
}
