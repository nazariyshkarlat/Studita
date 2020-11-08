package com.studita.presentation.listeners

import android.graphics.Rect
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.view.View.OnTouchListener

abstract class ViewOnTouch : OnTouchListener {
    /**
     * Flag determining whether the down touch has stayed with the bounds of the view.
     */

    private var touchStayedWithinViewBounds = false

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        view.clearAnimation()
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                touchStayedWithinViewBounds = true
                onDownTouchAction(event.x, event.y)
                true
            }
            MotionEvent.ACTION_UP -> {

                if (touchStayedWithinViewBounds) {
                    view.playSoundEffect(SoundEffectConstants.CLICK);
                    onUpTouchAction(event.x, event.y)
                }
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchStayedWithinViewBounds
                    && !isMotionEventInsideView(view, event)
                ) {
                    onCancelTouchAction(event.x, event.y)
                    touchStayedWithinViewBounds = false
                } else {
                    onMoveTouchAction(event.x, event.y)
                }
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                onCancelTouchAction(event.x, event.y)
                true
            }
            else -> false
        }
    }

    /**
     * Method which is called when the [View] is touched down.
     */
    abstract fun onDownTouchAction(x: Float, y: Float)

    /**
     * Method which is called when the down touch is released on the [View].
     */
    abstract fun onUpTouchAction(x: Float, y: Float)

    /**
     * Method which is called when the down touch is canceled,
     * e.g. because the down touch moved outside the bounds of the [View].
     */
    abstract fun onCancelTouchAction(x: Float, y: Float)

    /**
     * Method which is called when the down touch is moved on the [View]
     */

    abstract fun onMoveTouchAction(x: Float, y: Float)

    /**
     * Determines whether the provided [MotionEvent] represents a touch event
     * that occurred within the bounds of the provided [View].
     *
     * @param view  the [View] to which the [MotionEvent] has been dispatched.
     * @param event the [MotionEvent] of interest.
     * @return true iff the provided [MotionEvent] represents a touch event
     * that occurred within the bounds of the provided [View].
     */
    private fun isMotionEventInsideView(
        view: View,
        event: MotionEvent
    ): Boolean {
        val viewRect = Rect(
            view.left,
            view.top,
            view.right,
            view.bottom
        )
        return viewRect.contains(
            view.left + event.x.toInt(),
            view.top + event.y.toInt()
        )
    }

    internal inner class SingleTapConfirm : SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent): Boolean {
            return true
        }
    }
}