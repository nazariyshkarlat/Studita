package com.example.studita.utils

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import com.google.android.material.animation.AnimationUtils

private const val ANIMATION_SCALE_FROM_VALUE = 1.2f
private const val ANIMATION_ALPHA_FROM_VALUE = 0.3f
private const val ANIMATION_FADE_IN_DURATION = 500

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

fun View.animateFadeIn(delay: Long = 0) {
    val alphaAnimator: ValueAnimator? = getAlphaAnimator(ANIMATION_ALPHA_FROM_VALUE, 1f)
    val scaleAnimator: ValueAnimator? =
        getScaleAnimator(ANIMATION_SCALE_FROM_VALUE, 1f)
    val animatorSet = AnimatorSet()
    animatorSet.startDelay = delay
    animatorSet.playTogether(alphaAnimator, scaleAnimator)
    animatorSet.duration = ANIMATION_FADE_IN_DURATION.toLong()
    animatorSet.start()
}