package com.example.studita.presentation.views

import android.content.Context
import android.util.AttributeSet

class SquareMinSizeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (measuredWidth < measuredHeight) {
            setMeasuredDimension(measuredHeight, measuredHeight)
            redrawText()
        }
    }

    private fun redrawText() {
        text = text
    }
}