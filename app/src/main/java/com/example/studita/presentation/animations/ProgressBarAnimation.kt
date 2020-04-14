package com.example.studita.presentation.animations

import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar

class ProgressBarAnimation(private val progressBar: ProgressBar, from: Float, to: Float) : Animation() {
    private val from: Float = from * progressBar.max.toFloat()
    private val to: Float = if((to * progressBar.max.toFloat()) < progressBar.max) to * progressBar.max.toFloat() else progressBar.max.toFloat()
    override fun applyTransformation(
        interpolatedTime: Float,
        t: Transformation
    ) {
        super.applyTransformation(interpolatedTime, t)
        val value = (from + (to - from) * interpolatedTime).toInt()
        progressBar.progress = value
    }

}