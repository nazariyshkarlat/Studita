package com.example.studita.presentation.animations

import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar

class ProgressBarAnimation(private val progressBar: com.example.studita.presentation.views.ProgressBar, private val from: Float, private val to: Float) :
    Animation() {

    override fun applyTransformation(
        interpolatedTime: Float,
        t: Transformation
    ) {
        super.applyTransformation(interpolatedTime, t)
        val value = (from + (to - from) * interpolatedTime)
        progressBar.currentProgress = value
    }

}