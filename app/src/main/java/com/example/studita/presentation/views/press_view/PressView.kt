package com.example.studita.presentation.views.press_view

import android.content.res.Resources.Theme
import android.os.SystemClock
import android.util.TypedValue
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.annotation.AttrRes
import com.example.studita.presentation.listeners.ViewOnTouch


class PressView(val view: View, var withMinClickInterval: Boolean = false) {


    var onClick: (View) -> Unit = {}
    var pressAlpha = 0F
    private var animation: ViewPropertyAnimator? = null

    private var lastClickTime: Long = 0
    private var elapsedTime = 0L

    private val tooFastClick: Boolean
        get() {
            val currentClickTime = SystemClock.uptimeMillis()
            elapsedTime = currentClickTime - lastClickTime
            return elapsedTime <= MIN_CLICK_INTERVAL
        }


    companion object {
        private const val MIN_CLICK_INTERVAL: Long = 600
    }

    init {
        view.setOnTouchListener(object : ViewOnTouch() {
            override fun onDownTouchAction(x: Float, y: Float) {
                if (view.isEnabled && view.isClickable)
                    onDown()
            }

            override fun onUpTouchAction(x: Float, y: Float) {
                if (view.isEnabled && view.isClickable) {
                    onUp()

                    if (withMinClickInterval)
                        if (tooFastClick) return

                    lastClickTime = SystemClock.uptimeMillis()

                    onClick(view)
                }
            }

            override fun onCancelTouchAction(x: Float, y: Float) {
                if (view.isEnabled) {
                    onUp()
                }
            }

            override fun onMoveTouchAction(x: Float, y: Float) {}
        })
    }

    fun setPressAlpha(@AttrRes attr: Int) {
        val typedValue = TypedValue()
        val theme: Theme = view.context.theme
        theme.resolveAttribute(attr, typedValue, true)
        pressAlpha = typedValue.float
    }

    fun onDown() {
        view.clearAnimation()
        animation?.cancel()
        view.alpha = pressAlpha
    }

    fun onUp() {
        view.alpha = pressAlpha
        animation = view.animate().alpha(1F).setDuration(300)
        animation?.start()
    }
}