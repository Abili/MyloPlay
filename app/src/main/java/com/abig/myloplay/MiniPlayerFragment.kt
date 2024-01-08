package com.abig.myloplay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abig.myloplay.databinding.MiniPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
class MiniPlayerFragment : BottomSheetDialogFragment() {

    private var _binding: MiniPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MiniPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Your views are now accessible via binding
        binding.miniSongTitle.text = "Song title" // Set initial title as an example
        // Initialize other views as needed
    }

    fun updateMiniPlayer(song: Song) {
        // Update MiniPlayerFragment views with the selected song details
        binding.miniSongTitle.text = song.name
        // Update other views accordingly
    }

    fun showMiniPlayer() {
        // Make MiniPlayerFragment visible
        binding.root.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
