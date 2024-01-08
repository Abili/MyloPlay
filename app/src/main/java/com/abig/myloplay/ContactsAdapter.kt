package com.abig.myloplay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.ItemContactBinding
import com.bumptech.glide.Glide

class ContactsAdapter(private val onContactSelectedListener: (Boolean) -> Unit) :
    RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    private var contacts: List<User> = emptyList()
    private val selectedContacts = mutableSetOf<User>()

    fun setContacts(contacts: List<User>) {
        this.contacts = contacts
        notifyDataSetChanged()
    }

    fun getSelectedContacts(): Set<User> {
        return selectedContacts
    }

    fun clearSelectedContacts() {
        selectedContacts.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position], selectedContacts.contains(contacts[position]))

        holder.itemView.setOnClickListener {
            val contact = contacts[position]
            val isSelected = toggleSelection(contact)
            onContactSelectedListener.invoke(isSelected)
        }
    }

    override fun getItemCount(): Int = contacts.size

    private fun toggleSelection(contact: User): Boolean {
        val isSelected = selectedContacts.contains(contact)
        if (isSelected) {
            selectedContacts.remove(contact)
        } else {
            selectedContacts.add(contact)
        }
        notifyItemChanged(contacts.indexOf(contact))
        return !isSelected
    }

    class ContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: User, isSelected: Boolean) {
            binding.apply {
                Glide.with(binding.root)
                    .load(contact.imageUrl)
                    .placeholder(R.drawable.ic_user)
                    .into(contactImageView)

                usernameTextView.text = contact.username
                phoneTextView.text = contact.phone

                // Update UI based on the selection state
                itemView.isSelected = isSelected
                if (isSelected) {
                    // Add visual indication for selected state, e.g., change background color or overlay
                    itemView.setBackgroundResource(R.color.selectedContactBackground)
                } else {
                    // Clear visual indication for unselected state
                    itemView.setBackgroundResource(android.R.color.transparent)
                }
            }
        }
    }
}
