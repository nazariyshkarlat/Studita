package com.example.studita.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

class PressTextView@JvmOverloads constructor(context: Context,
                                             attrs: AttributeSet? = null,
                                             defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr){
    private val pressViewImpl = PressView(this)

    fun setOnClickListener(work: (View) -> Unit){
        pressViewImpl.onClick = work
    }
}
