package com.example.studita.presentation.listeners

import androidx.core.widget.NestedScrollView

class FabScrollImpl(private val fabScrollListener: FabScrollListener) :
    NestedScrollView.OnScrollChangeListener {

    private var scrollDist = 0
    private var isVisible = true
    private val MINIMUM = 0

    override fun onScrollChange(
        v: NestedScrollView?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        val dy = scrollY - oldScrollY
        fabScrollListener.onScroll(scrollY)
        if (isVisible && scrollDist > MINIMUM) {
            fabScrollListener.hide()
            scrollDist = 0
            isVisible = false
        } else if (!isVisible && scrollDist < -MINIMUM) {
            fabScrollListener.show()
            scrollDist = 0
            isVisible = true
        }

        if (isVisible && dy > 0 || !isVisible && dy < 0) {
            scrollDist += dy
        }
    }
}