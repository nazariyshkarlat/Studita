package com.example.studita.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

class PressLinearLayout @JvmOverloads constructor(context: Context,
                                                   attrs: AttributeSet? = null,
                                                   defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val pressViewImpl = PressView(this)
    init {
        pressViewImpl.alpha = 0.34F
    }

    fun setOnClickListener(work: (View) ->Unit) {
        pressViewImpl.onClick = work
    }
}