package com.abig.myloplay

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuthHelper(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private var verificationId: String? = null

    fun sendVerificationCode(
        phoneNumber: String,
        onComplete: (Boolean) -> Unit
    ) {
        if (phoneNumber == "") {
            Toast.makeText(context, "PhoneNumber Required", Toast.LENGTH_SHORT).show()
        } else {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(context as Activity)
                .setCallbacks(callbacks(onComplete, phoneNumber))
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }

    }

    private fun callbacks(onComplete: (Boolean) -> Unit, phoneNumber: String) =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential, phoneNumber, onComplete)
            }

            override fun onVerificationFailed(exception: FirebaseException) {
                runOnUiThread {
                    Toast.makeText(
                        context,
                        "Verification Failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    onComplete(false)
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                this@PhoneAuthHelper.verificationId = verificationId
                runOnUiThread {
                    if (context is VerificationCodeActivity) {
                        // Finish the current VerificationCodeActivity
                        (context as Activity).finish()
                    }
                    Toast.makeText(context, "Verification code sent", Toast.LENGTH_SHORT).show()
                    //savePhoneNumberToPrefs(phoneNumber)

                    // Launch VerificationActivity on successful code sent
                    val intent = Intent(context, VerificationCodeActivity::class.java)
                    intent.putExtra("verificationId", verificationId)
                    intent.putExtra("phone", phoneNumber)
                    intent.putExtra("signInMethod","phone")
                    context.startActivity(intent)
                    (context as Activity).finish()
                }
            }

        }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        phoneNumber: String,
        onComplete: (Boolean) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    runOnUiThread {
                        val intent = Intent(context, UserProfile::class.java)
                        intent.putExtra("signInMethod", "phone")
                        intent.putExtra("phone", phoneNumber)
                        context.startActivity(intent)
                        onComplete(true)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            context,
                            "Verification Failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        onComplete(false)
                    }
                }
            }
    }

    private fun runOnUiThread(action: () -> Unit) {
        val handler = Handler(Looper.getMainLooper())
        handler.post(action)
    }


}
