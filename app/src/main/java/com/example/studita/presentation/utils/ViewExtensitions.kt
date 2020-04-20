package com.example.studita.presentation.utils

import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.example.studita.presentation.animations.ProgressBarAnimation
import com.example.studita.presentation.listeners.FabScrollImpl
import com.example.studita.presentation.listeners.FabScrollListener
import com.example.studita.presentation.listeners.OnViewSizeChangeListener
import com.example.studita.presentation.listeners.OnViewSizeChangeListenerImpl
import com.example.studita.presentation.views.CustomProgressBar

fun NestedScrollView.setOnScrollChangeFabListener(fabScrollListener: FabScrollListener) {
    this.setOnScrollChangeListener(FabScrollImpl(fabScrollListener))
}

fun View.setOnViewSizeChangeListener(viewSizeChangeListener: OnViewSizeChangeListener): View.OnLayoutChangeListener {
    val listener = OnViewSizeChangeListenerImpl(viewSizeChangeListener, this)
    this.addOnLayoutChangeListener(listener)
    return listener
}

fun ViewGroup.makeView(@LayoutRes layoutResId: Int): View =
    LayoutInflater.from(this.context).inflate(layoutResId, this, false)

fun View.onViewCreated(block: () ->Unit){
    this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
        override fun onGlobalLayout() {
            block()
            this@onViewCreated.viewTreeObserver.removeOnGlobalLayoutListener(this);
        }
    })
}

fun View.getAppCompatActivity(): AppCompatActivity?{
    var context = this.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun CustomProgressBar.animateProgress(toPercent: Float, onAnimEnd: ()->Unit = {}, duration: Long = 300L, delay: Long = 0L, fromPercent: Float = this.percentProgress) {
    val progressAnimation = ProgressBarAnimation(
        this,
        fromPercent,
        toPercent
    )
    progressAnimation.startOffset = delay
    progressAnimation.interpolator =
        androidx.interpolator.view.animation.FastOutSlowInInterpolator()
    progressAnimation.duration = duration
    progressAnimation.setAnimationListener(object : Animation.AnimationListener{
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            this@animateProgress.clearAnimation()
            onAnimEnd.invoke()
        }
        override fun onAnimationStart(animation: Animation?) {}
    })
    this.startAnimation(progressAnimation)
}