package com.example.studita.utils

import android.animation.ValueAnimator
import android.view.View
import com.google.android.material.animation.AnimationUtils

fun View.getAlphaAnimator(vararg alphaValues: Float): ValueAnimator? {
    val animator = ValueAnimator.ofFloat(*alphaValues)
    animator.interpolator = AnimationUtils.LINEAR_INTERPOLATOR
    animator.addUpdateListener { valueAnimator ->
        this.alpha = valueAnimator.animatedValue as Float
    }
    return animator
}

fun View.getScaleAnimator(vararg scaleValues: Float): ValueAnimator? {
    val animator = ValueAnimator.ofFloat(*scaleValues)
    animator.interpolator = AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR
    animator.addUpdateListener { valueAnimator ->
        val scale = valueAnimator.animatedValue as Float
        scaleX = scale
        scaleY = scale
    }
    return animator
}