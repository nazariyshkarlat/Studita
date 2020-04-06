package com.example.studita.presentation.views.press_view

import android.content.res.Resources.Theme
import android.util.TypedValue
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.annotation.AttrRes
import com.example.studita.presentation.listeners.ViewOnTouch


class PressView(val view: View){


    var onClick: (View) -> Unit = {}
    var pressAlpha = 0F
    private var animation: ViewPropertyAnimator? = null

    init {
        view.setOnTouchListener(object: ViewOnTouch(){
            override fun onDownTouchAction(x: Float, y: Float)  {
                onDown()
            }

            override fun onUpTouchAction(x: Float, y: Float)  {
                if (view.isEnabled) {
                    onUp()
                    onClick(view)
                }
            }

            override fun onCancelTouchAction(x: Float, y: Float)  {
                if (view.isEnabled) {
                    onUp()
                }
            }

            override fun onMoveTouchAction(x: Float, y: Float) {
            }
        })
    }

    fun setPressAlpha(@AttrRes attr: Int){
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