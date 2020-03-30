package com.example.studita.presentation.draw

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.ImageView
import com.example.studita.presentation.utils.dpToPx
import com.example.studita.presentation.utils.spToPx
import java.lang.reflect.Array
import kotlin.random.Random

object AwaDrawer{
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
    private var color = 0

    var r = Random

    init {
        textPaint.setARGB(255, 255, 255, 255)
    }

    fun drawAwa(image: ImageView, name: String) {

        circlePaint.color = Color.parseColor(
            colors[r.nextInt(
                colors.size - 1)])

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 16.spToPx().toFloat()

        canvas.drawCircle(
            imageSize / 2F, imageSize / 2F, imageSize / 2F,
            circlePaint
        )
        canvas.drawText(name.first().toString().toUpperCase(), imageSize / 2F, ((canvas.height/ 2) - ((textPaint.descent() + textPaint.ascent()) / 2)),
            textPaint
        )

        image.setImageBitmap(roundBitmap)
    }

}