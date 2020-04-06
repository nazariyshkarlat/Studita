package com.example.studita.presentation.listeners

import android.view.View
import android.view.ViewTreeObserver

class OnViewSizeChangeListenerImpl(private val viewSizeChangeListener: OnViewSizeChangeListener, private val view: View): View.OnLayoutChangeListener{

    override fun onLayoutChange(
        v: View?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {
        viewSizeChangeListener.onViewSizeChanged(view)
    }
}