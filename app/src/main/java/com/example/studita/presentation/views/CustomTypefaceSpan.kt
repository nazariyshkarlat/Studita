package com.example.studita.presentation.views

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

class CustomTypefaceSpan(family: String?, private val newType: Typeface?) :
    TypefaceSpan(family) {
    override fun updateDrawState(ds: TextPaint) {
        newType?.let { applyCustomTypeFace(ds, it) }
    }

    override fun updateMeasureState(paint: TextPaint) {
        newType?.let { applyCustomTypeFace(paint, it) }
    }

    companion object {
        private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
            val oldStyle: Int
            val old = paint.typeface
            oldStyle = old?.style ?: 0
            val fake = oldStyle and tf.style.inv()
            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }
            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }
            paint.typeface = tf
        }
    }

}