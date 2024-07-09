package com.abig.myloplay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abig.myloplay.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_playlists -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, AllPlaylistsFragment())
//                        .commit()
                    true
                }

                R.id.action_downloads -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, PoolActivity())
                        .commit()
                    true
                }

                else -> false
            }
        }

        // Select the default fragment
        binding.bottomNavigationView.selectedItemId = R.id.action_playlists
    }
}
