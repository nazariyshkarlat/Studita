package com.example.studita.presentation.views.press_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.example.studita.R

class PressTextView@JvmOverloads constructor(context: Context,
                                             attrs: AttributeSet? = null,
                                             defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), IPressView{
    private val pressViewImpl =
        PressView(this)

    init {
        pressViewImpl.setPressAlpha(R.attr.press_view_press_alpha)
    }

    override fun setOnClickListener(work: (View) -> Unit){
        pressViewImpl.onClick = work
    }
}
