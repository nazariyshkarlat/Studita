package com.example.studita.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.example.studita.R

class ErrorEditText @JvmOverloads constructor(context: Context,
attrs: AttributeSet? = null,
defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatEditText(context, attrs, defStyleAttr) {
    private val stateError = intArrayOf(R.attr.state_error)
    var hasError = false
        set(value){
            field = value
            post{refreshDrawableState()}
        }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (hasError) {
            View.mergeDrawableStates(drawableState, stateError)
        }
        return drawableState
    }
}