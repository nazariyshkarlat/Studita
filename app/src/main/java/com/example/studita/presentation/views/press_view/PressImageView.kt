package com.example.studita.presentation.views.press_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.example.studita.R
import com.example.studita.presentation.views.press_view.PressView

class PressImageView@JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val  pressViewImpl =
        PressView(this)

    init {
        pressViewImpl.setPressAlpha(R.attr.press_view_press_alpha)
    }

    fun setOnClickListener(work: (View) ->Unit) {
        pressViewImpl.onClick = work
    }
}