package com.abig.myloplay

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import java.lang.ref.WeakReference

object MiniPlayerManager {
    private var contextRef: WeakReference<Context>? = null

    fun initializeMiniPlayer(
        context: Context,
        parentLayout: ViewGroup,
        miniSongTitle: TextView,
        miniSongArtist: TextView,
        miniPlayBtn: ImageView,
        miniSeekBar: SeekBar,
        miniSongDuration: TextView
    ) {
        // Store a weak reference to the context
        contextRef = WeakReference(context)

        // Create the mini player layout
        val miniPlayerLayout = LinearLayout(contextRef?.get())
        miniPlayerLayout.orientation = LinearLayout.HORIZONTAL
        miniPlayerLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        miniPlayerLayout.setPadding(16, 16, 16, 16)

        // Add mini player UI elements to the layout
        miniPlayerLayout.addView(miniSongTitle)
        miniPlayerLayout.addView(miniSongArtist)
        miniPlayerLayout.addView(miniPlayBtn)
        miniPlayerLayout.addView(miniSeekBar)
        miniPlayerLayout.addView(miniSongDuration)

        // Add mini player layout to the specified parent layout
        parentLayout.addView(miniPlayerLayout)
    }
}
