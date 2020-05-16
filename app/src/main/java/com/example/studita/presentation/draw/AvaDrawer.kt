package com.example.studita.presentation.draw

import android.graphics.*
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.example.studita.R
import com.example.studita.utils.dpToPx
import com.example.studita.utils.spToPx
import java.lang.Math.abs
import kotlin.random.Random


object AvaDrawer{
    private val imageSize = 36.dpToPx()
    private val textSize = 20.spToPx()
    private val textPaint = Paint()
    private val circlePaint = Paint()
    private val roundBitmap = Bitmap.createBitmap(
        imageSize,
        imageSize, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(roundBitmap)
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

    fun drawAwa(image: ImageView, name: String) {

        val text = name.first().toString()

        val r = Rect()
        canvas.getClipBounds(r)
        val cHeight = r.height()
        val cWidth = r.width()

        circlePaint.color = Color.parseColor(
            colors[kotlin.math.abs(name.hashCode() % colors.size)])

        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = textSize.toFloat()
        textPaint.typeface = ResourcesCompat.getFont(image.context, R.font.roboto_regular)

        canvas.drawCircle(
            imageSize / 2F, imageSize / 2F, imageSize / 2F,
            circlePaint
        )

        textPaint.getTextBounds(text, 0, text.length, r)

        val x: Float = cWidth / 2f - r.width() / 2f - r.left
        val y: Float = cHeight / 2f + r.height() / 2f - r.bottom

        canvas.drawText(text, x, y, textPaint)

        image.setImageBitmap(roundBitmap)
    }

}