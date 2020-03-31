package com.example.studita.presentation.draw

import android.graphics.*
import android.widget.ImageView
import com.example.studita.presentation.utils.dpToPx
import com.example.studita.presentation.utils.spToPx
import kotlin.random.Random


object AvaDrawer{
    private val imageSize = 36.dpToPx()
    private val textPaint = Paint()
    private val circlePaint = Paint()
    private val roundBitmap = Bitmap.createBitmap(
        imageSize,
        imageSize, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(roundBitmap)
    private var colors = arrayListOf("#5E97F6",
        "#9CCC65",
        "#FF8A65",
        "#9E9E9E",
        "#9FA8DA",
        "#90A4AE",
        "#AED581",
        "#F6BF26",
        "#FFA726",
        "#4DD0E1",
        "#BA68C8",
        "#A1887F"
    )

    var random = Random

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
            colors[random.nextInt(
                colors.size - 1)])

        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = 16.spToPx().toFloat()

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