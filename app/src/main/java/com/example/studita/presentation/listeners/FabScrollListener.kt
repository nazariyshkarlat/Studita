package com.example.studita.presentation.listeners

import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

interface FabScrollListener {

    fun show()
    fun hide()
    fun onScroll(scrollY: Int)
}

