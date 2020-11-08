package com.studita.presentation.draw

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.studita.R
import com.studita.utils.dpToPx
import com.studita.utils.spToPx


object AvaDrawer {
    private val imageSize = 56F.dpToPx()
    private val textSize = 28F.spToPx()
    private val textPaint = Paint()
    private val circlePaint = Paint()
    private var colors = arrayListOf(
        "#9CCC65",
        "#C2175B",
        "#0098A6",
        "#5856D6",
        "#FF2D55",
        "#5E97F6",
        "#FF8A65",
        "#FF9500",
        "#5AC8FA",
        "#FF6347",
        "#FF1493",
        "#9400D3",
        "#008B8B"
    )

    init {
        textPaint.setARGB(255, 255, 255, 255)
    }

    fun drawAvatar(image: ImageView, name: String, userId: Int) {

        val d = BitmapDrawable(image.resources, getBitmap(name, userId, image.context))
        d.setAntiAlias(true)
        d.isFilterBitmap = true

        image.setImageDrawable(d)
    }

    fun getBitmap(name: String, userId: Int, context: Context): Bitmap {

        val roundBitmap = Bitmap.createBitmap(
            imageSize,
            imageSize, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(roundBitmap)

        val text = name.first().toString()

        val r = Rect()
        canvas.getClipBounds(r)
        val cHeight = r.height()
        val cWidth = r.width()

        circlePaint.color = Color.parseColor(
            colors[kotlin.math.abs(userId.toString().hashCode() % colors.size)]
        )
        circlePaint.isAntiAlias = true

        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = textSize.toFloat()
        textPaint.typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
        textPaint.isAntiAlias = true

        canvas.drawCircle(
            imageSize / 2F, imageSize / 2F, imageSize / 2F,
            circlePaint
        )

        textPaint.getTextBounds(text, 0, text.length, r)

        val x: Float = cWidth / 2f - r.width() / 2f - r.left
        val y: Float = cHeight / 2f + r.height() / 2f - r.bottom

        canvas.drawText(text, x, y, textPaint)

        return roundBitmap
    }

}