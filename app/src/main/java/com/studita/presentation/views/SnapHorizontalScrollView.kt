package com.studita.presentation.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.core.view.children
import com.studita.utils.childContainsParentX
import com.studita.utils.getRelativeLeft
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
open class SnapHorizontalScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr), View.OnTouchListener {

    companion object {
        private const val SWIPE_MIN_DISTANCE = 5
        private const val SWIPE_THRESHOLD_VELOCITY = 300
    }

    var activeFeatureIndex = 0

    private val gestureDetector =
        GestureDetector(context, SnapHorizontalScrollViewGestureDetector())

     init {
        post {
            this.scrollTo(0, 0)
            this.setOnTouchListener(this)
        }
    }

    var onTouchDownX = 0
    override fun onTouch(v: View, event: MotionEvent): Boolean {

        if(event.action == MotionEvent.ACTION_DOWN)
            onTouchDownX = scrollX

        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            scrollView((scrollX - onTouchDownX) > 0)
            true
        } else {
            false
        }
    }

    private inner class SnapHorizontalScrollViewGestureDetector :
        GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            try {
                if (abs(e1.x - e2.x) > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    scrollView((e1.x - e2.x) > 0)
                    return true
                }
            } catch (e: Exception) {
                Log.e("Fling", "There was an error processing the Fling event:" + e.message)
            }
            return false
        }
    }

    private fun scrollView(scrollToRight: Boolean){
        activeFeatureIndex = getCurrentActiveFeatureByScrollX(scrollX, scrollToRight)
        val scrollTo = getScrollTo(activeFeatureIndex)
        smoothScrollTo(scrollTo, 0)
    }

    private fun getScrollTo(activeFeatureIndex: Int) =
        (this.getChildAt(0) as ViewGroup).getChildAt(activeFeatureIndex)
            .getRelativeLeft(this.getChildAt(0) as ViewGroup)

    private fun getCurrentActiveFeatureByScrollX(scrollX: Int, scrollToRight: Boolean) =
        (this.getChildAt(0) as ViewGroup).children.indexOfFirst {
            it.childContainsParentX(((scrollX + if(scrollToRight) (it.measuredWidth/1.2F) else (it.measuredWidth/5F)).toInt()))
        }
}