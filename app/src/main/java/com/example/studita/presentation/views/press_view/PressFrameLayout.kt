package com.example.studita.presentation.views.press_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.example.studita.R

class PressFrameLayout @JvmOverloads constructor(context: Context,
                                                  attrs: AttributeSet? = null,
                                                  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IPressView {

    private val pressViewImpl =
        PressView(this)

    init {
        pressViewImpl.setPressAlpha(R.attr.press_view_press_alpha)
    }

    override fun setOnClickListener(work: (View) ->Unit) {
        pressViewImpl.onClick = work
    }
}