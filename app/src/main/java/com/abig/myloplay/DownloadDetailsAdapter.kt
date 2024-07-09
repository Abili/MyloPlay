package com.abig.myloplay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.ItemDownloadDetailBinding

class DownloadDetailsAdapter() :
    RecyclerView.Adapter<DownloadDetailsAdapter.DetailViewHolder>() {
    private val details = mutableListOf<AudioFile>()

    fun setSong(songs: List<AudioFile>) {
        details.addAll(songs)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding =
            ItemDownloadDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(details[position])
    }

    override fun getItemCount(): Int = details.size

    inner class DetailViewHolder(private val binding: ItemDownloadDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(detail: AudioFile) {
            binding.apply {
                textViewName.text = detail.title
                textViewArtist.text = detail.artist
                textViewDuration.text = detail.duration
                // Load image using Glide or any other image loading library
                // Example: Glide.with(itemView.context).load(detail.imageUrl).into(imageView)
            }
        }
    }
}
