package com.example.studita.presentation.listeners

import android.os.SystemClock
import android.view.View


abstract class OnSingleClickListener : View.OnClickListener {

    companion object {
        private const val MIN_CLICK_INTERVAL: Long = 600

        fun View.setOnSingleClickListener(block: (View) -> Unit) {
            setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    block.invoke(this@setOnSingleClickListener)
                }
            })

        }
    }

    private var lastClickTime: Long = 0

    abstract fun onSingleClick(v: View)

    override fun onClick(v: View) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - lastClickTime
        if (elapsedTime <= MIN_CLICK_INTERVAL) return
        lastClickTime = currentClickTime
        onSingleClick(v)
    }
}