package com.abig.myloplay

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.abig.myloplay.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var fileSizeSeekBar: SeekBar
    private lateinit var fileSizeReference: TextView
    private lateinit var preferences: SharedPreferences
    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fileSizeSeekBar = findViewById(R.id.fileSizeSeekBar)
        fileSizeReference = findViewById(R.id.fileSizeReference)

        preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        // Set the initial progress of the SeekBar based on stored value
        var maxFileSizeBytes =
            preferences.getInt("maxFileSizeBytes", 100 * 1024 * 1024) // Default: 100MB
        binding.fileSizeSeekBar.progress = convertBytesToProgress(maxFileSizeBytes)

        // Update the file size reference text
        updateFileSizeReference(maxFileSizeBytes)

        binding.fileSizeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Convert progress to maxFileSizeBytes and update the reference text
                maxFileSizeBytes = convertProgressToBytes(progress)
                updateFileSizeReference(maxFileSizeBytes)

                // Store the updated maxFileSizeBytes in SharedPreferences
                preferences.edit().putInt("maxFileSizeBytes", maxFileSizeBytes).apply()

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.signOutButton.setOnClickListener {
            // Handle sign out and navigate to the MainActivity
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun convertBytesToProgress(maxFileSizeBytes: Int): Int {
        // Convert maxFileSizeBytes to progress value
        // Adjust this calculation based on your step sizes
        return when (maxFileSizeBytes) {
            100 * 1024 * 1024 -> 0 // 100MB
            300 * 1024 * 1024 -> 1 // 300MB
            500 * 1024 * 1024 -> 2 // 500MB
            700 * 1024 * 1024 -> 3 // 700MB
            else -> 4 // 1GB (default)
        }
    }

    private fun convertProgressToBytes(progress: Int): Int {
        // Convert progress value to maxFileSizeBytes
        return when (progress) {
            0 -> 100 * 1024 * 1024 // 100MB
            1 -> 300 * 1024 * 1024 // 300MB
            2 -> 500 * 1024 * 1024 // 500MB
            3 -> 700 * 1024 * 1024 // 700MB
            else -> 1024 * 1024 * 1024 // 1GB (default)
        }
    }

    private fun updateFileSizeReference(maxFileSizeBytes: Int) {
        // Update the file size reference text based on the selected maxFileSizeBytes
        val fileSizeText = when (maxFileSizeBytes) {
            100 * 1024 * 1024 -> "\t100MB \t\t 300MB \t\t 500MB \t\t\t 700MB \t\t\t 1GB"
            300 * 1024 * 1024 -> "\t100MB \t\t 300MB \t\t 500MB \t\t\t 700MB \t\t\t 1GB"
            500 * 1024 * 1024 -> "\t100MB \t\t 300MB \t\t 500MB \t\t\t 700MB \t\t\t 1GB"
            700 * 1024 * 1024 -> "\t100MB \t\t 300MB \t\t 500MB \t\t\t 700MB \t\t\t 1GB"
            else -> "\t100MB \t\t 300MB \t\t 500MB \t\t\t 700MB \t\t\t 1GB" // Default: 1GB
        }
        fileSizeReference.text = fileSizeText
    }
}