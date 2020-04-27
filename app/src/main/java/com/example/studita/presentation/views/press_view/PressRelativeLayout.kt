package com.example.studita.presentation.views.press_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.example.studita.R

class PressRelativeLayout @JvmOverloads constructor(context: Context,
                                                  attrs: AttributeSet? = null,
                                                  defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val pressViewImpl =
        PressView(this)

    init {
        pressViewImpl.setPressAlpha(R.attr.press_view_press_alpha_lighter)
    }

    fun setOnClickListener(work: (View) ->Unit) {
        pressViewImpl.onClick = work
    }
}