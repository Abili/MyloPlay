package com.abig.myloplay

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abig.myloplay.databinding.ActivityUserProfileBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class UserProfile : AppCompatActivity() {

    // ViewBinding variable for the activity layout
    private lateinit var binding: ActivityUserProfileBinding

    // FirebaseAuth instance
    private lateinit var auth: FirebaseAuth
    private var imageUri: Uri? = null // Nullable Uri for the profile picture

    // FirebaseDatabase instance
    private lateinit var database: FirebaseDatabase
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var phone: String
    private lateinit var phoneNumber: String
    private lateinit var firstName: String
    private lateinit var lastName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize FirebaseAuth and FirebaseDatabase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()


        val signInMethod = intent.getStringExtra("signInMethod")

        if (signInMethod == "phone") {
            //binding.verifPhone!!.visibility = View.VISIBLE
            handlePhoneSignUp()
        }

        // Open the image files
        openImageFiles()

        // Set up the floating action button
        binding.imageView.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher?.launch(intent)
        }

        phoneNumber = binding.editTextPhone.text?.toString()!!.trim()
        binding.profileCreateButton.setOnClickListener {
            firstName = binding.editTextTextPersonFirstName.text.toString().trim()
            lastName = binding.editTextTextPersonLastName.text.toString().trim()
            username = "$firstName $lastName"
            email = binding.editTextTextEmailAddress.text.toString().trim()
            //phone = binding.editTextPhone.text.toString().trim()
            val countryCode = binding.ccp.selectedCountryCode

            if (phoneNumber.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                phone = "+$countryCode$phoneNumber"

                // Send verification code
                PhoneAuthHelper(this).sendVerificationCode(phone) { success ->
                    if (success) {
                        binding.progressBar.visibility = View.GONE
                        showSnackbar("Verification Successful")
                    } else {
                        showSnackbar("Verification failed. Try Again")
                    }
                }
            } else {
                showSnackbar("PhoneNumber Required!")
            }

            // Validate input and handle registration
            if (phone.isEmpty() || username.isEmpty() || email.isEmpty()) {
                showSnackbar("Empty Field, Check and try again!!")
            } else {
                validateInput(
                    username, email, phone
                )
            }
        }
    }

    private fun showSnackbar(s: String) {
        Snackbar.make(binding.root, s, Snackbar.LENGTH_SHORT).show()
    }

    private fun handlePhoneSignUp() {
        val phoneNumber = binding.editTextPhone.text.toString().trim()
        phone = intent.getStringExtra("phone").toString()
        if (phone.startsWith("+256")) {
            val newPhone = phone.replace("+256", "")
            binding.editTextPhone.setText(newPhone)
            //binding.verifEmail!!.visibility = View.VISIBLE
        } else {
            binding.editTextPhone.setText(phone)
        }

    }


    private fun openImageFiles() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    imageUri = result.data?.data!!
                    Glide.with(binding.imageView).load(imageUri).centerCrop()
                        .placeholder(R.drawable.placeholder_with).into(binding.imageView)
                }
            }
    }

    private fun uploadImage(downloadUri: Uri?) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val imageUrl = downloadUri?.toString() // Keep image URL as nullable
        val user = User(
            userId, imageUrl!!, username, phone, email
        )

        // Add the user to the Firebase database
        val usrRef = FirebaseDatabase.getInstance().reference.child("users")
        user.id = userId

        usrRef.child(userId).setValue(user).addOnCompleteListener {
            Snackbar.make(
                binding.root, "Profile Created", Snackbar.LENGTH_SHORT
            ).show()

            lifecycleScope.launch {
                val handler = Handler()
                handler.postDelayed({
                    binding.progressBar.visibility = View.VISIBLE
                    startActivity(Intent(this@UserProfile, AllPlaylists::class.java))
                    finish()
                }, 0)
            }

        }.addOnFailureListener {
            Snackbar.make(
                binding.root, it.message.toString(), Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun noImage() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        //val imageUrl = downloadUri?.toString() // Keep image URL as nullable
        val user = User(
            userId, null, username, phone, email
        )

        // Add the user to the Firebase database
        val usrRef = FirebaseDatabase.getInstance().reference.child("users")
        user.id = userId

        usrRef.child(userId).setValue(user).addOnCompleteListener {
            Snackbar.make(
                binding.root, "Profile Created", Snackbar.LENGTH_SHORT
            ).show()

            lifecycleScope.launch {
                val handler = Handler()
                handler.postDelayed({
                    binding.progressBar.visibility = View.VISIBLE
                    startActivity(Intent(this@UserProfile, AllPlaylists::class.java))
                    finish()
                }, 0)
            }

        }.addOnFailureListener {
            Snackbar.make(
                binding.root, it.message.toString(), Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun validateInput(username: String, email: String, phone: String) {
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show()
            return
        }

        //val useremail = binding.editTextTextEmailAddress.text.toString()
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return
        }

        // Continue with registration
        handleRegistration()

    }

    private fun handleRegistration() {
        // Check if the user has uploaded a profile picture
        if (imageUri != null) {
            // Upload the profile picture to Firebase Storage
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("userimages/${imageUri!!.lastPathSegment}")
            val uploadTask = imageRef.putFile(imageUri!!)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Image uploaded successfully, get the download URL
                    val downloadUrl = task.result
                    // Save downloadUrl to database
                    uploadImage(downloadUrl)
                } else {
                    // Handle failure
                    Snackbar.make(
                        binding.root, "Failed To Create Profile !!", Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            // User didn't upload a profile picture, proceed with registration
            noImage()
            Snackbar.make(binding.root, "Please wait Creating Profile ...", Snackbar.LENGTH_SHORT)
                .show()
        }
    }
}
