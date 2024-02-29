package com.abig.myloplay


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abig.myloplay.databinding.ActivitySignUpBinding
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.hbb20.CountryCodePicker
import java.io.File

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        //FirebaseApp.initializeApp(this)
//        Firebase.database.setPersistenceEnabled(true)


        // Set up click listener for phone authentication
        binding.phoneAuth.setOnClickListener {
            signIn("phone")
            binding.phoneauthLayout!!.visibility = View.VISIBLE
            binding.verifyCode!!.visibility = View.VISIBLE
            binding.phoneAuth.visibility = View.GONE
        }

        // Set up click listener for the "Sign Up" button
        binding.verifyCode!!.setOnClickListener {
            binding.phoneAuth.visibility = View.VISIBLE
            binding.verifyCode!!.visibility = View.GONE

            // Get the phone number from the form
            //val countryCodePicker: CountryCodePicker = findViewById(R.id.ccp)
            val countryCode = binding.ccp.selectedCountryCode
            val phoneNumber = binding.editTextPhone.text?.toString()!!.trim()

            if (phoneNumber.isNotEmpty()) {
                binding.signInProgress.visibility = View.VISIBLE
                val phone = "+$countryCode$phoneNumber"

                // Send verification code
                PhoneAuthHelper(this).sendVerificationCode(phone) { success ->
                    if (success) {
                        binding.signInProgress.visibility = View.GONE
                        Snackbar.make(
                            binding.root, "Verification Successful", Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        if (binding.phoneAuth.visibility == View.GONE) {
                            binding.phoneAuth.visibility = View.VISIBLE
                        }
                        Snackbar.make(
                            binding.root, "Verification failed. Try Again", Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Snackbar.make(
                    binding.root, "PhoneNumber Required!", Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        // Set up click listener for sign-out
        binding.buttonSignOut.setOnClickListener {
            signOut()
        }


    }


    private fun signIn(method: String) {
        // Store sign-in method in shared preferences
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("signInMethod", method)
        editor.apply()

        // Store sign-in state
        editor.putBoolean("isSignedIn", true)
        editor.apply()
    }


    @SuppressLint("StringFormatInvalid")
    private fun updateUI(currentUser: FirebaseUser?, downloadUrl: Uri?) {
        if (currentUser != null) {
            binding.textViewStatus.text = getString(R.string.status_signed_in, currentUser.email)
            binding.phoneAuth.visibility = View.GONE
            binding.buttonSignOut.visibility = View.VISIBLE

            // Successfully signed in, navigate to UserProfile
            // ...
            val intent = Intent(this, UserProfile::class.java)
            intent.putExtra("signInMethod", getSignInMethod())
            intent.putExtra("imageUrl", downloadUrl.toString()) // Pass the download URL as a string
            intent.putExtra("email", currentUser.email)
            intent.putExtra("name", currentUser.displayName)
            startActivity(intent)
            finish()
            // ...

            Toast.makeText(
                this, "You are signed in as: ${currentUser.displayName}", Toast.LENGTH_SHORT
            ).show()
            finish()
        } else {
            binding.textViewStatus.text = getString(R.string.status_signed_out)
            binding.phoneAuth.visibility = View.VISIBLE
            binding.buttonSignOut.visibility = View.GONE

            Toast.makeText(
                this, "Authentication failed.", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun signOut() {
        auth.signOut()
        updateUI(auth.currentUser, auth.currentUser!!.photoUrl)
    }

    private fun getSignInMethod(): String {
        // Retrieve sign-in method from shared preferences
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("signInMethod", "") ?: ""
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
