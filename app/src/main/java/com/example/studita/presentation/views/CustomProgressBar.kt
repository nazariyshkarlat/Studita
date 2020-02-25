package com.example.studita.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar

class CustomProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ProgressBar(context, attrs, defStyleAttr) {

    var percentProgress: Int
        set(value){
            progress = value * (max / 100)
        }
        get() {
            return progress/(max/100)
        }
}