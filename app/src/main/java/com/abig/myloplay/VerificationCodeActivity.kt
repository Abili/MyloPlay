package com.abig.myloplay

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.abig.myloplay.databinding.ActivityVerificationCodeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class VerificationCodeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityVerificationCodeBinding
    lateinit var screen: String

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVerificationCodeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val phone = intent.getStringExtra("phone")
        val signInMethod = intent.getStringExtra("signInMethod")


        binding.buttonVerifyCode.setOnClickListener {
            binding.resendCode.visibility = View.VISIBLE
            val verificationCode = binding.editTextVerificationCode.text.toString().trim()

            if (verificationCode.isNotEmpty()) {
                // If verification code is entered, manually verify
                val verificationId = intent.getStringExtra("verificationId")

                if (verificationId != null) {
                    val credential =
                        PhoneAuthProvider.getCredential(verificationId, verificationCode)
                    signInWithPhoneAuthCredential(credential, phone, signInMethod)
                } else {
                    showToast("Verification ID is null")
                }
            } else {
                binding.editTextVerificationCode.error = "Enter the verification code"
            }
        }
        binding.resendCode.setOnClickListener {

            PhoneAuthHelper(this).sendVerificationCode(phone!!) { success ->
                if (success) {
                    binding.resendCode.visibility = View.GONE
                    Snackbar.make(
                        binding.root, "Code Resent Successful", Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }


    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        phone: String?,
        signInMethod: String?
    ) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, navigate to the next activity or perform desired action
                // For simplicity, we'll just display a success toast
                        // Handle unknown screen
                        val intent = Intent(this, UserProfile::class.java)
                        intent.putExtra("phone", phone)
                        intent.putExtra("signInMethod", signInMethod)
                        startActivity(intent)
                        finish()
                        showToast("Authentication successful")



            } else {
                // Sign in failed, display a message to the user
                binding.resendCode.visibility = View.VISIBLE
                showToast("Authentication failed: ${task.exception?.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
