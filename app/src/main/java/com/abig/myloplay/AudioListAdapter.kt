package com.abig.myloplay

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.ItemSongBinding

class AudioListAdapter(
    private val onRemoveClickListener: (AudioFile) -> Unit,
    private val onRecommendClickListener: (AudioFile) -> Unit
) :
    RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {
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
                // Handle playlist item click here

                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedSong = songs[position]
                    showPlayerBottomSheet(selectedSong)
                }

                //val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    val selectedSong = songs[position]
//                    // Implement logic to display songs for the selected playlist
//                    val intent = Intent(itemView.context, MiniPlayerActivity::class.java)
//                    intent.putStringArrayListExtra(
//                        MiniPlayerActivity.PLAYLIST,
//                        ArrayList(songs.map { it.downloadUrl })
//                    )
//                    intent.putExtra(MiniPlayerActivity.CURRENT_POSITION, position)
//                    intent.putExtra(MiniPlayerActivity.SONG_ID, selectedSong.downloadUrl)
//                    intent.putStringArrayListExtra(
//                        MiniPlayerActivity.SONG_TITLE,
//                        ArrayList(songs.map { it.title })
//                    )
//                    intent.putStringArrayListExtra(
//                        MiniPlayerActivity.ARTIST,
//                        ArrayList(songs.map { it.artist })
//                    )
//                    intent.putExtra(MiniPlayerActivity.SONG_DURATION, selectedSong.duration)
//                    itemView.context.startActivity(intent)
//                }
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
                                val selectedSong = songs[position]
                                // Pass the download URL to the AudioActivity
                                (view.context as? AudioActivity)?.downloadSong(
                                    selectedSong.downloadUrl!!,
                                    selectedSong.title
                                )
                            }
                            true // Return true to indicate that the item is handled
                        }

                        R.id.song_recommend -> {
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                val selectedSong = songs[position]
                                // Call the recommend click listener
                                onRecommendClickListener.invoke(selectedSong)
                            }
                            true
                        }

                        R.id.deleteSong -> {
                            // Handle the recommend action
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                val selectedSong = songs[position]
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

        fun bind(song: AudioFile) {
            binding.textTitle.text = song.title
            binding.textDuration.text = song.duration.toString()
            binding.textArtist.text = song.artist.toString()
            binding.textTitle.isSelected = true

        }

         fun showPlayerBottomSheet(selectedSong: AudioFile) {
            val bottomSheetFragment = MiniPlayerActivity()
            val bundle = Bundle()
            // Pass necessary data to the bottom sheet fragment
            bundle.putString(MyloPlayer.SONG_ID, selectedSong.downloadUrl)
            bundle.putStringArrayList(
                MiniPlayerActivity.SONG_TITLE,
                ArrayList(songs.map { it.title })
            )
            bundle.putStringArrayList(
                MiniPlayerActivity.PLAYLIST,
                ArrayList(songs.map { it.downloadUrl })
            )
            bundle.putStringArrayList(
                MiniPlayerActivity.ARTIST,
            ArrayList(songs.map { it.artist })
            )
            bundle.putStringArrayList(
                MiniPlayerActivity.SONG_DURATION,
                ArrayList(songs.map { it.duration })
            )
            // Add any other necessary data to the bundle
            bottomSheetFragment.arguments = bundle

            bottomSheetFragment.show(
                (itemView.context as AppCompatActivity).supportFragmentManager,
                bottomSheetFragment.tag
            )
        }


        private fun showRemoveConfirmationDialog(song: AudioFile) {
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
    }


}
