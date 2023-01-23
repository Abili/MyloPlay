package com.abig.myloplay

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.abig.myloplay.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    // ViewBinding variable for the login form layout
    private lateinit var binding: ActivityMainBinding

    // FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            // User is already signed in, skip the sign-up screen
            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)
        }
        // Set up click listener for the "Sign Up" button
        binding.buttonSignUp.setOnClickListener {
            // Get the email and password from the form
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            // Create a new account with the email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Account creation successful
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                        startActivity(Intent(this, UserProfile::class.java))
                    } else {
                        // Account creation failed
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
        }

        // Set up click listener for the "Log In" button
        binding.buttonLogIn.setOnClickListener {
            // Get the email and password from the form
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            // Log in with the email and password
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Log in successful
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                        startActivity(Intent(this, HomeActivity::class.java))
                    } else {
                        // Log in failed
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
        }
    }

    // Update the UI based on the current user
    @SuppressLint("StringFormatInvalid")
    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            // User is signed in, show the main app screen
            binding.textViewStatus.text = getString(R.string.status_signed_in, currentUser.email)
            binding.buttonSignUp.visibility = View.GONE
            binding.buttonLogIn.visibility = View.GONE
            binding.buttonSignOut.visibility = View.VISIBLE
        } else {
            // User is signed out, show the login form
            binding.textViewStatus.text = getString(R.string.status_signed_out)
            binding.buttonSignUp.visibility = View.VISIBLE
            binding.buttonLogIn.visibility = View.VISIBLE
            binding.buttonSignOut.visibility = View.GONE
        }
    }

    // Sign out the current user
    private fun signOut() {
        auth.signOut()
        updateUI(null)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

