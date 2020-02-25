package com.example.studita.presentation.views

import android.view.View
import android.view.ViewPropertyAnimator
import com.example.studita.presentation.listeners.ViewOnTouch

class PressView(val view: View){

    init {

        view.setOnTouchListener(object: ViewOnTouch(){
            override fun onDownTouchAction() {
                onDown()
            }

            override fun onUpTouchAction() {
                if (view.isEnabled) {
                    onUp()
                    onClick(view)
                }
            }

            override fun onCancelTouchAction() {
                if (view.isEnabled) {
                    onUp()
                }
            }
    })
    }

    var onClick: (View) -> Unit = {}
    var alpha = 0.62F
    private var animation: ViewPropertyAnimator? = null

    fun onDown() {
        view.clearAnimation()
        animation?.cancel()
        view.alpha = alpha
    }

    fun onUp() {
        view.alpha = alpha
        animation = view.animate().alpha(1F).setDuration(300)
        animation?.start()
    }
}