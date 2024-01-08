package com.abig.myloplay

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.ActivityUserContactsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserContacts : AppCompatActivity() {

    private lateinit var binding: ActivityUserContactsBinding
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var contactsRetriever: ContactsRetriever
    private var areContactsSelected = false // State variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactsAdapter = ContactsAdapter { areContactsSelected ->
            onContactsSelected(areContactsSelected)
        }
        binding.contactsRecyclerView.adapter = contactsAdapter
        binding.contactsRecyclerView.layoutManager = LinearLayoutManager(this)

        contactsRetriever = ContactsRetriever(this)

        // Rest of your activity logic
        val songUrl = intent.getStringExtra("songUrl")
        val songTitle = intent.getStringExtra("songTitle")
        val artist = intent.getStringExtra("artist")
        val duration = intent.getStringExtra("duration",)

        // Check and request contacts permission
        checkAndRequestContactsPermission()

        // button click listener to perform an action on selected contacts
        binding.acceptRecommend.setOnClickListener {
            performActionOnSelectedContacts(songUrl, songTitle, artist, duration)
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
                            val user = User(userId!!, "", username, phoneNumber, "")
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun performActionOnSelectedContacts(
        songUrl: String?,
        songTitle: String?,
        artist: String?,
        duration: String?
    ) {
        // Get the selected contacts from the adapter
        val selectedContacts = contactsAdapter.getSelectedContacts()

        // Perform your action on the selected contacts
        for (contact in selectedContacts) {
            // Example: Save recommendation to Firebase
            saveRecommendationToFirebase(contact, songUrl, songTitle, artist, duration)
        }

        showToast("Action performed on selected contacts")

        // Reset the contact selection state
        areContactsSelected = false
        contactsAdapter.clearSelectedContacts()

        // Update the visibility of other buttons based on contact selection state
        updateButtonsVisibility()
    }

    private fun saveRecommendationToFirebase(
        contact: User,
        songUrl: String?,
        songTitle: String?,
        artist: String?,
        duration: String?
    ) {
        // Save recommendation to Firebase under the user's node
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val recommendationRef = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(contact.id)
            .child("recommendations")
        val pushId = recommendationRef.push().key

        val recommendation = Recommendation(
            pushId,
            songUrl!!,
            songTitle!!,
            artist!!,
            duration!!,
            currentUserId
        )

        recommendationRef.child(pushId!!).setValue(recommendation)
    }

    private fun onContactsSelected(areContactsSelected: Boolean) {
        // Update the contact selection state
        this.areContactsSelected = areContactsSelected

        // Update the visibility of other buttons based on contact selection state
        updateButtonsVisibility()
    }

    private fun updateButtonsVisibility() {
        if (areContactsSelected) {
            binding.recommendBtns.visibility = View.VISIBLE
        } else {
            binding.recommendBtns.visibility = View.GONE
        }
    }

    private fun showButtons() {
        // Perform action when the "Show Buttons" button is clicked
        showToast("Show Buttons clicked")
    }

    private fun hideButtons() {
        // Perform action when the "Hide Buttons" button is clicked
        showToast("Hide Buttons clicked")
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
