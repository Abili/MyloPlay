package com.abig.myloplay

import ContactsPrivacyAdapter
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.ActivityUserPrivacyContactsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserPrivacyContacts : AppCompatActivity(){

    private lateinit var binding: ActivityUserPrivacyContactsBinding
    private lateinit var contactsAdapter: ContactsPrivacyAdapter
    private lateinit var contactsRetriever: ContactsRetriever
    private var areContactsSelected = false // State variable
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPrivacyContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("RestrictedUsers", Context.MODE_PRIVATE)

        contactsAdapter = ContactsPrivacyAdapter(
            { areContactsSelected ->
                onContactsSelected(areContactsSelected)
            },
            sharedPreferences
        )

        binding.contactsRecyclerView.adapter = contactsAdapter
        binding.contactsRecyclerView.layoutManager = LinearLayoutManager(this)

        contactsRetriever = ContactsRetriever(this)


        // Check and request contacts permission
        checkAndRequestContactsPermission()

        // button click listener to perform an action on selected contacts
        binding.confirm.setOnClickListener {
            performActionOnSelectedContacts()
        }

        // Example: Set visibility of other buttons based on contact selection state
//        binding.showButtonsButton.setOnClickListener {
//            showButtons()
//        }

        // Example: Set visibility of other buttons based on contact selection state
//        binding.hideButtonsButton.setOnClickListener {
//            hideButtons()
//        }
    }

    private fun checkAndRequestContactsPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS
            ) -> {
                // Permission already granted, retrieve contacts
                retrieveContacts()
            }

            else -> {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CONTACTS_PERMISSION
                )
            }
        }
    }

    private fun retrieveContacts() {
        val contacts = contactsRetriever.retrieveContacts()

        val contactsFromFirebase = mutableListOf<User>()

        // Retrieve contacts from Firebase
        FirebaseDatabase.getInstance().reference.child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapshot in snapshot.children) {
                        val phoneNumber = userSnapshot.child("phone").getValue(String::class.java)
                        val username = userSnapshot.child("username").getValue(String::class.java)
                        val userId = userSnapshot.child("id").getValue(String::class.java)

                        if (!phoneNumber.isNullOrBlank() && !username.isNullOrBlank()) {
                            // Keep the formatting of the phone number from Firebase
                            val isSelected = loadSelectionState(userId!!)
                            val user = User(userId, "", username, phoneNumber, "", isSelected)
                            contactsFromFirebase.add(user)
                        }
                    }

                    // Compare and display matching contacts
                    val matchingContacts = contactsFromFirebase.filter { firebaseContact ->
                        contacts.any { contact ->
                            val formattedDevicePhoneNumber = if (contact.phone.startsWith("0")) {
                                // Convert phone number starting with zero to +256 format
                                "+256${contact.phone.substring(1).replace(" ", "")}"
                            } else {
                                // Keep the existing format if it already starts with +256
                                contact.phone.replace(" ", "")
                            }

                            // Check for a match
                            formattedDevicePhoneNumber == firebaseContact.phone
                        }
                    }
                    contactsAdapter.setContacts(matchingContacts)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
    }

    private fun loadSelectionState(contactId: String): Boolean {
        // Retrieve the existing set of selected contacts from SharedPreferences
        val selectedContactsSet = sharedPreferences.getStringSet("", mutableSetOf())

        // Check if the contact is in the set, indicating it was previously selected
        return selectedContactsSet?.contains(contactId) == true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun performActionOnSelectedContacts() {
        // Get the selected contacts from the adapter
        val selectedContacts = contactsAdapter.getSelectedContacts()

        if (selectedContacts.isNotEmpty()) {
            // Perform your action on the selected contacts
            for (contact in selectedContacts) {
                // Update the restricted node in Firebase under the user's node
                saveRecommendationToFirebase(contact, contact.isSelected)
            }

            showToast("Playlist Privacy updated")

            // Reset the contact selection state
            areContactsSelected = false
            contactsAdapter.clearSelectedContacts()

            // Update the visibility of other buttons based on contact selection state
            updateButtonsVisibility()
        } else {
            showToast("No contacts selected.")
        }
    }

    private fun saveRecommendationToFirebase(contact: User, isChecked: Boolean?) {
        // Save recommendation to Firebase under the user's node
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val restrictedRef =
            FirebaseDatabase.getInstance().reference.child("users").child(currentUserId)
                .child("restricted").child(contact.id) // Use the contact's ID as the key

        restrictedRef.setValue(isChecked).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Show a toast message indicating privacy status update
                val toastMessage =
                    "User ${if (isChecked == true) "restricted" else "unrestricted"}"
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            } else {
                // Handle the error case
                Toast.makeText(this, "Failed to update privacy status", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun updateRestrictedStatusInSharedPreferences(contactId: String, isSelected: Boolean) {
        // Retrieve the existing set of restricted users from SharedPreferences
        val restrictedUsersSet = sharedPreferences.getStringSet("restrictedUsers", mutableSetOf())

        // Update the set based on the selected status
        if (isSelected) {
            restrictedUsersSet?.add(contactId)
        } else {
            restrictedUsersSet?.remove(contactId)
        }

        // Save the updated set back to SharedPreferences
        sharedPreferences.edit().putStringSet("restrictedUsers", restrictedUsersSet).apply()
    }


    private fun onContactsSelected(areContactsSelected: Boolean) {
        // Update the contact selection state
        this.areContactsSelected = areContactsSelected

        // Update the visibility of other buttons based on contact selection state
        updateButtonsVisibility()
    }


    private fun updateButtonsVisibility() {
        // Check if there are any selected contacts
        val anySelectedContacts = contactsAdapter.getSelectedContacts().isNotEmpty()

        if (anySelectedContacts || areContactsSelected) {
            binding.recommendBtns.visibility = View.VISIBLE
        } else {
            binding.recommendBtns.visibility = View.GONE
        }
    }



    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CONTACTS_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, retrieve contacts
                retrieveContacts()
            } else {
                // Permission denied, handle accordingly
                finish()
            }
        }
    }

    companion object {
        private const val REQUEST_CONTACTS_PERMISSION = 1
    }



}
