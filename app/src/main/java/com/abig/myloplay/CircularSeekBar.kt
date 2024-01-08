package com.abig.myloplay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

// CircularSeekBar.java


class CircularSeekBar : View {
    private var paint: Paint? = null
    private var rectF: RectF? = null
    private var progress = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        paint = Paint()
        paint!!.isAntiAlias = true
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeWidth = 20f // Adjust stroke width as needed
        paint!!.color = resources.getColor(R.color.black) // Set color as needed
        rectF = RectF()
        progress = 0
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2
        val centerY = height / 2
        val radius = Math.min(centerX, centerY) - 10 // Adjust padding as needed
        rectF!![(centerX - radius).toFloat(), (centerY - radius).toFloat(), (centerX + radius).toFloat()] =
            (centerY + radius).toFloat()
        val angle = 360 * progress / 100.0f
        canvas.drawArc(rectF!!, -90f, angle, false, paint!!)
    }

    fun setProgress(progress: Int) {
        if (progress in 0..100) {
            this.progress = progress
            invalidate()
        }
    }
}

