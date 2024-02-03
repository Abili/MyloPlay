import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.R
import com.abig.myloplay.User
import com.abig.myloplay.databinding.ItemPrivacyContactBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ContactsPrivacyAdapter(
    private val onContactSelectedListener: (Boolean) -> Unit,
    private val sharedPreferences: SharedPreferences,
    //private val onContactCheckedChangeListener: OnContactCheckedChangeListener,

) : RecyclerView.Adapter<ContactsPrivacyAdapter.ContactViewHolder>() {

    private var contacts: List<User> = emptyList()
    private val selectedContacts = mutableSetOf<User>()
    lateinit var binding: ItemPrivacyContactBinding


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

    interface OnContactCheckedChangeListener {
        fun onContactCheckedChange(contact: User, isChecked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        binding =
            ItemPrivacyContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)

        // Set item click listener
        holder.itemView.setOnClickListener {
            holder.toggleSelection(contact)
            onContactSelectedListener.invoke(contact.isSelected!!)
            //onContactCheckedChangeListener.onContactCheckedChange(contact, isSelected)
        }

    }

    override fun getItemCount(): Int = contacts.size

    private fun toggleSelection(contact: User): Boolean {
        val isSelected = !contact.isSelected!!
        contact.isSelected = isSelected

        if (isSelected) {
            selectedContacts.add(contact)
        } else {
            selectedContacts.add(contact)
        }

        notifyDataSetChanged() // Notify data set changed to update UI
        return isSelected
    }


    inner class ContactViewHolder(private val binding: ItemPrivacyContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: User) {
            binding.apply {
                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
                val restrictedRef = FirebaseDatabase.getInstance().reference
                    .child("users")
                    .child(currentUserId)
                    .child("restricted")
                    .child(contact.id)  // Assuming contact.id is the key in the restricted node

                restrictedRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val isRestricted =
                            snapshot.exists() && snapshot.getValue(Boolean::class.java) == true

                        Glide.with(binding.root)
                            .load(contact.imageUrl)
                            .placeholder(R.drawable.ic_user)
                            .into(contactImageView)

                        usernameTextView.text = contact.username
                        phoneTextView.text = contact.phone

                        // Update UI based on the selection state
                        itemView.isSelected = isRestricted
                        checkmarkImageView.visibility = if (isRestricted) View.VISIBLE else View.INVISIBLE



                        // Handle checkbox state changes
                        //toggleSelection(contact)
                        //onContactSelectedListener.invoke(contact.isSelected!!)
                        //onContactCheckedChangeListener.onContactCheckedChange(contact, contact.isSelected!!)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle errors
                    }
                })
            }
        }

        fun toggleSelection(contact: User) {
            val isSelected = !contact.isSelected!!
            contact.isSelected = isSelected
            binding.checkmarkImageView.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
            if (isSelected) {
                selectedContacts.add(contact)
            } else {
                selectedContacts.add(contact)
            }
        }
    }
}
