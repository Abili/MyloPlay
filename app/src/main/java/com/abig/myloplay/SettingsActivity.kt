package com.abig.myloplay

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.setting_preference, rootKey)
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            return when (preference.key) {
                "sign_out_preference" -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(requireContext(), SignUp::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                    true
                }

                "privacy_preference" -> {
                    val intent = Intent(requireContext(), UserPrivacyContacts::class.java)
                    startActivity(intent)
                    true
                }

                "theme_preference" -> {
                    showThemeDialog()
                    return true
                }

                else -> return super.onPreferenceTreeClick(preference)
            }
        }

        private fun showThemeDialog() {
            val currentTheme = AppCompatDelegate.getDefaultNightMode()
            val options = arrayOf("System Default", "Dark", "Light")
            val selectedIndex = when (currentTheme) {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> 0
                AppCompatDelegate.MODE_NIGHT_YES -> 1
                AppCompatDelegate.MODE_NIGHT_NO -> 2
                else -> 0 // Default to System Default if unknown
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Select Theme")
                .setSingleChoiceItems(options, selectedIndex) { dialog, which ->
                    val newTheme = when (which) {
                        0 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        1 -> AppCompatDelegate.MODE_NIGHT_YES
                        2 -> AppCompatDelegate.MODE_NIGHT_NO
                        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }
                    AppCompatDelegate.setDefaultNightMode(newTheme)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}
