package com.example.studita.presentation.views.press_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.example.studita.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PressFab @JvmOverloads constructor(context: Context,
                                         attrs: AttributeSet? = null,
                                         defStyleAttr: Int = 0
) : FloatingActionButton(context, attrs, defStyleAttr) {
    private val pressViewImpl =
        PressView(this)

    init {
        pressViewImpl.setPressAlpha(R.attr.press_view_press_alpha)
    }

    fun setOnClickListener(work: (View) ->Unit) {
        pressViewImpl.onClick = work
    }
}
