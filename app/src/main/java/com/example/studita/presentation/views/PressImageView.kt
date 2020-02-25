package com.example.myapplication.ui.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.example.studita.presentation.views.PressView

class PressImageView@JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val  pressViewImpl = PressView(this)

    fun setOnClickListener(work: (View) ->Unit) {
        pressViewImpl.onClick = work
    }
}