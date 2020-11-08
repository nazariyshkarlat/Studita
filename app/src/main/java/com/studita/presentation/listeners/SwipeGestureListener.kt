package com.studita.presentation.listeners

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


open class SwipeGestureListener : SimpleOnGestureListener() {

    companion object {

        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }


    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        var result = false
        try {
            val diffY = e1?.y?.let { e2?.y?.minus(it) }
            val diffX = e1?.x?.let { e2?.x?.minus(it) }

            if (diffX != null && diffY != null) {

                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        result = true
                    }
                }
            } else {
                onSwipeRight()

                result = true
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        return result
    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
}