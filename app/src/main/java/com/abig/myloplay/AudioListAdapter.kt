// AudioListAdapter.kt
package com.abig.myloplay

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.ItemSongBinding

class AudioListAdapter(
    private val onRemoveClickListener: ((AudioFile) -> Unit)?,
    private val onRecommendClickListener: ((AudioFile) -> Unit)?,
    private val onClickListener: ((MutableList<AudioFile>, position: Int) -> Unit)?
) : RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {

    private val songs = mutableListOf<AudioFile>()

    interface OnShuffleClickListener {
        fun onShuffleClick(shuffledSongs: List<AudioFile>)
    }

    fun getShuffledSongs(): List<AudioFile> {
        val shuffledSongs = songs.toMutableList()
        shuffledSongs.shuffle()
        return shuffledSongs
    }

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

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickListener?.invoke(songs, position)
                    val context = itemView.context
                    val intent = Intent(context, MiniPlayerActivity::class.java).apply {
                        putStringArrayListExtra(MiniPlayerActivity.PLAYLIST, ArrayList(songs.map { it.downloadUrl }))
                        putStringArrayListExtra(MiniPlayerActivity.SONG_TITLE, ArrayList(songs.map { it.title }))
                        putStringArrayListExtra(MiniPlayerActivity.ARTIST, ArrayList(songs.map { it.artist }))
                        putStringArrayListExtra(MiniPlayerActivity.SONG_DURATION, ArrayList(songs.map { it.duration }))
                        putExtra(MiniPlayerActivity.CURRENT_POSITION, position)
                        putParcelableArrayListExtra(MiniPlayerActivity.SONG_LISTS, ArrayList(songs))
                    }
                    context.startActivity(intent)
                }
            }
        }

        fun bind(song: AudioFile) {
            binding.textTitle.text = song.title
            binding.textArtist.text = song.artist
            binding.textTitle.isSelected = true
        }

        private fun showRemoveConfirmationDialog(song: AudioFile) {
            val context = binding.root.context
            AlertDialog.Builder(context)
                .setTitle("Remove Song")
                .setMessage("Are you sure you want to remove this song from the playlist?")
                .setPositiveButton("Remove") { _, _ ->
                    onRemoveClickListener?.invoke(song)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
