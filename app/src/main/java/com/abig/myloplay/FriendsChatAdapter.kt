package com.abig.myloplay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.FriendsChatModelBinding

class FriendsChatAdapter(
) :
    RecyclerView.Adapter<FriendsChatAdapter.ViewHolder>() {
    private val msgs = mutableListOf<Message>()


    fun setData(msg: MutableList<Message>) {
        msgs.clear()
        msgs.addAll(msg)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FriendsChatModelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = msgs[position]
        holder.bind(msg)

    }

    override fun getItemCount(): Int {
        return msgs.size
    }

    inner class ViewHolder(private val binding: FriendsChatModelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.username.text = message.username
            binding.msg.text = message.msg
            binding.chattime.text = message.chatTime


        }


    }


}
