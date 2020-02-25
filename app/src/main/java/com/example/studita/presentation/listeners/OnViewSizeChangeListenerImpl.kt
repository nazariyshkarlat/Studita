package com.example.studita.presentation.listeners

import android.view.View
import android.view.ViewTreeObserver

class OnViewSizeChangeListenerImpl(private val viewSizeChangeListener: OnViewSizeChangeListener, private val view: View): ViewTreeObserver.OnGlobalLayoutListener{
    override fun onGlobalLayout() {
        viewSizeChangeListener.onViewSizeChanged(view)
    }
}