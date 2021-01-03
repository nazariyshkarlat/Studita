package com.studita.presentation.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ImageSpan

class CenteredImageSpan(context: Context, resourceId: Int): ImageSpan(context, resourceId) {

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.save()

        val transY = (bottom - top) / 2 - drawable.bounds.height() / 2

        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }

}