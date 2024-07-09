package com.abig.myloplay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.ItemDownloadedFileBinding

class DownloadedFilesAdapter(/*private val listener: FolderClickListener*/) : RecyclerView.Adapter<DownloadedFilesAdapter.FileViewHolder>() {
    private val files = mutableListOf<FileString>()

    fun addFile(files: List<FileString>) {
        this.files.addAll(files)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemDownloadedFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(files[position])
    }

    override fun getItemCount(): Int = files.size

    inner class FileViewHolder(private val binding: ItemDownloadedFileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(file: FileString) {
            binding.fileNameTextView.text = file.name
//            itemView.setOnClickListener {
//                listener.onFolderClick(file)
//            }
        }
    }

    interface FolderClickListener {
        fun onFolderClick(folder: FileString)
    }
}
