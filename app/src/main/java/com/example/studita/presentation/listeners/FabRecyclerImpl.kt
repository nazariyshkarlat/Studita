package com.example.studita.presentation.listeners

import androidx.recyclerview.widget.RecyclerView

open class FabRecyclerImpl(private val fabScrollListener: FabScrollListener) : RecyclerView.OnScrollListener(){

    private var scrollDist = 0
    private var isVisible = true
    private val MINIMUM = 0
    private var scrollY = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        scrollY += dy
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