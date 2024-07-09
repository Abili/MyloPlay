package com.abig.myloplay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.ChatModelBinding
import com.abig.myloplay.databinding.FriendsChatModelBinding

class CurrentUserChatAdapter(private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<Message>()

    fun setMessages(messages: List<Message>) {
        this.messages.clear()
        this.messages.addAll(messages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val binding =
                ChatModelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            UserMessageViewHolder(binding)
        } else {
            val binding =
                FriendsChatModelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FriendMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder.itemViewType == VIEW_TYPE_USER) {
            (holder as UserMessageViewHolder).bind(message)
        } else {
            (holder as FriendMessageViewHolder).bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) {
            VIEW_TYPE_USER
        } else {
            VIEW_TYPE_FRIEND
        }
    }

    inner class UserMessageViewHolder(private val binding: ChatModelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.apply {
                username.text = message.username
                msg.text = message.msg
                chatime.text =
                    Util.formatTime(message.chatTime!!.toLong()) // Format timestamp as desired

                if (message.senderId == currentUserId) {
                    // Check if the message has been seen
                    if (message.seen) {
                        binding.msgRead.visibility = View.VISIBLE // Double tick icon
                    } else {
                        binding.msgSent.visibility = View.VISIBLE // Single tick icon
                    }
                } else if (message.reciepientId == currentUserId) {
                    // For messages received by the current user, always show the double tick icon
                    binding.msgRead.visibility = View.VISIBLE // Double tick icon
                }

            }
        }
    }

    inner class FriendMessageViewHolder(private val binding: FriendsChatModelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.apply {
                username.text = message.username
                msg.text = message.msg
                chattime.text =
                    Util.formatTime(message.chatTime!!.toLong())// Format timestamp as desired

            }
        }
    }

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_FRIEND = 2
    }
}
