package com.example.studita.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.core.view.children
import com.example.studita.utils.childContainsParentX
import com.example.studita.utils.getRelativeLeft
import kotlin.math.abs

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

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            activeFeatureIndex = getCurrentActiveFeatureByScrollX(scrollX)
            val scrollTo = getScrollTo(activeFeatureIndex)
            smoothScrollTo(scrollTo, 0)
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
                if (e1.x - e2.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    activeFeatureIndex =
                        if (activeFeatureIndex < getItemsCount() - 1) activeFeatureIndex + 1 else getItemsCount() - 1
                    smoothScrollTo(getScrollTo(activeFeatureIndex), 0)
                    return true
                } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    activeFeatureIndex = if (activeFeatureIndex > 0) activeFeatureIndex - 1 else 0
                    smoothScrollTo(getScrollTo(activeFeatureIndex), 0)
                    return true
                }
            } catch (e: Exception) {
                Log.e("Fling", "There was an error processing the Fling event:" + e.message)
            }
            return false
        }
    }

    private fun getScrollTo(activeFeatureIndex: Int) =
        (this.getChildAt(0) as ViewGroup).getChildAt(activeFeatureIndex)
            .getRelativeLeft(this.getChildAt(0) as ViewGroup)

    private fun getCurrentActiveFeatureByScrollX(scrollX: Int) =
        (this.getChildAt(0) as ViewGroup).indexOfChild((this.getChildAt(0) as ViewGroup).children.first {
            it.childContainsParentX(scrollX + it.measuredWidth)
        })

    private fun getItemsCount() = (getChildAt(this.activeFeatureIndex) as ViewGroup).childCount

}