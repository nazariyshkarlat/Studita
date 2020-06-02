package com.example.studita.presentation.draw

import android.R.attr.bitmap
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.example.studita.R
import com.example.studita.utils.dpToPx
import com.example.studita.utils.spToPx


object AvaDrawer{
    private val imageSize = 56.dpToPx()
    private val textSize = 28.spToPx()
    private val textPaint = Paint()
    private val circlePaint = Paint()
    private var colors = arrayListOf("#C2175B",
        "#0098A6",
        "#AA47BC",
        "#00887A",
        "#689F39",
        "#90A4AE",
        "#63AA55",
        "#41A4A6",
        "#F5511E",
        "#512DA7",
        "#DD4554",
        "#9CCC65",
        "#5E97F6",
        "#FF8A65",
        "#9FA8DA",
        "#FFA726",
        "#BA68C8",
        "#A1887F"
    )

    init {
        textPaint.setARGB(255, 255, 255, 255)
    }

    fun drawAvatar(image: ImageView, name: String, userId: Int) {

        val roundBitmap = Bitmap.createBitmap(
                imageSize,
                imageSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(roundBitmap)

        val text = name.first().toString()

        val r = Rect()
        canvas.getClipBounds(r)
        val cHeight = r.height()
        val cWidth = r.width()

        circlePaint.color = Color.parseColor(
            colors[kotlin.math.abs(userId.toString().hashCode() % colors.size)])
        circlePaint.isAntiAlias = true

        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = textSize.toFloat()
        textPaint.typeface = ResourcesCompat.getFont(image.context, R.font.roboto_regular)
        textPaint.isAntiAlias = true

        canvas.drawCircle(
            imageSize / 2F, imageSize / 2F, imageSize / 2F,
            circlePaint
        )

        textPaint.getTextBounds(text, 0, text.length, r)

        val x: Float = cWidth / 2f - r.width() / 2f - r.left
        val y: Float = cHeight / 2f + r.height() / 2f - r.bottom

        canvas.drawText(text, x, y, textPaint)

        val d = BitmapDrawable(image.resources, roundBitmap)
        d.setAntiAlias(true)
        d.isFilterBitmap = true

        image.setImageDrawable(d)
    }

}