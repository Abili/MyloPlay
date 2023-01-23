package com.abig.myloplay

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.abig.myloplay.databinding.ActivityUserProfileBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class UserProfile : AppCompatActivity() {

    // ViewBinding variable for the activity layout
    private lateinit var binding: ActivityUserProfileBinding

    // FirebaseAuth instance
    private lateinit var auth: FirebaseAuth
    val imageUri = mutableStateOf<Uri?>(null)

    // FirebaseDatabase instance
    private lateinit var database: FirebaseDatabase
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var phone: String
    private lateinit var firstName: String
    private lateinit var lastName: String

    // RecyclerView adapter for displaying the playlists
    private lateinit var adapter: OwnersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth and FirebaseDatabase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //val username = firstName + lastName
        //open the audio files
        openAudioFiles()
        // Set up the RecyclerView
        // ...
        // Initialize the adapters for the RecyclerViews

        if (auth.currentUser != null) {
            // User is already signed in, skip the sign-up screen
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        // setValue to the users node
        database.reference.child("users")

        // Set up the floating action button
        binding.imageView.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher!!.launch(intent)

        }
        binding.profileCreateButton.setOnClickListener {
            firstName = binding.editTextTextPersonFirstName.text.toString()
            lastName = binding.editTextTextPersonLastName.text.toString()
            val username = firstName + lastName
            email = binding.editTextTextEmailAddress.text.toString()
            validateInput(username, email)

        }
    }

    private fun openAudioFiles() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    imageUri.value = result.data!!.data
                    Glide.with(binding.imageView)
                        .load(imageUri.value)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_with)
                        .into(binding.imageView)

                }
            }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_SONGS = 3
        private const val TAG = "HomeActivity"
    }

    private fun uploadImage(imageUri: MutableState<Uri?>) {
        val firstName = binding.editTextTextPersonFirstName.text.toString()
        //var imageUrl = binding.imageView.setImageURI(imageUri.value)
        val lastName = binding.editTextTextPersonLastName.text.toString()
        val phone = binding.editTextPhone.text.toString()
        val email = binding.editTextTextEmailAddress.text.toString()
        val username = "$firstName $lastName"
        val user = User(
            FirebaseAuth.getInstance().currentUser!!.uid,
            imageUri.value.toString(),
            username,
            phone,
            email
        )

        // Add the playlist to the Firebase database
        val playlistRef = FirebaseDatabase.getInstance().reference.child("users")
        val userId = playlistRef.push().key
        user.id = userId!!
        if (!(firstName.isEmpty() || lastName.isEmpty()
                    || email.isEmpty()
                    || phone.isEmpty())
        ) {
            playlistRef.child(userId).setValue(user)
                .addOnCompleteListener {
                    Snackbar.make(
                        binding.root,
                        "Profile Created",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()

                    lifecycleScope.launch {
                        val handler = Handler()
                        handler.postDelayed({
                            binding.progressBar.visibility = View.VISIBLE
                            startActivity(Intent(this@UserProfile, HomeActivity::class.java))
                        }, 5000)
                    }

                }.addOnFailureListener {
                    Snackbar.make(
                        binding.root,
                        it.message.toString(),
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
        } else {
            Snackbar.make(
                binding.root,
                "Fields Required",
                Snackbar.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun isValidEmail(email: String) =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun validateInput(username: String, email: String): Boolean {
        if (username.isEmpty() || email.isEmpty()) {
            return false
        }
        // check if the email is in the correct format
        this.email = binding.editTextTextEmailAddress.text.toString()
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return false
        }
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
        if (!this.email.matches(emailRegex.toRegex())) {
            return false
        }
        // check if the username already exists in the Firebase Database
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        usersRef.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // username already exists
                        Snackbar.make(
                            binding.root,
                            "UserName exists already Exists",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })

        // check if the email already exists in the Firebase Database
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // email already exists
                    Snackbar.make(binding.root, "Email already Exists", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    uploadImage(imageUri)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
        return true
    }

    private fun validatePhone(phone: String): Boolean {
        val phonePattern = Patterns.PHONE
        return phonePattern.matcher(phone).matches()
    }

}
