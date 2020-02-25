package com.example.studita.presentation.animations

import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar

class ProgressBarAnimation(private val progressBar: ProgressBar, from: Int, to: Int) : Animation() {
    private val from: Int = from * (progressBar.max / 100)
    private val to: Int = to * (progressBar.max / 100)
    override fun applyTransformation(
        interpolatedTime: Float,
        t: Transformation
    ) {
        super.applyTransformation(interpolatedTime, t)
        val value = (from + (to - from) * interpolatedTime).toInt()
        progressBar.progress = value
    }

}