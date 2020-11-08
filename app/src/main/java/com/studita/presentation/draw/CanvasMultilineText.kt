package com.studita.presentation.draw

import android.graphics.Canvas
import android.os.Build
import android.text.*
import androidx.annotation.RequiresApi
import androidx.core.graphics.withTranslation
import androidx.core.util.lruCache

fun getMultilineTextLayout(
    text: CharSequence,
    textPaint: TextPaint,
    width: Int,
    start: Int = 0,
    end: Int = text.length,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    spacingMult: Float = 1f,
    spacingAdd: Float = 0f,
    includePad: Boolean = true,
    ellipsizedWidth: Int = width,
    ellipsize: TextUtils.TruncateAt? = null
): StaticLayout {

    val cacheKey = "$text-$start-$end-$textPaint-$width-$alignment-" +
            "$spacingMult-$spacingAdd-$includePad-$ellipsizedWidth-$ellipsize"

    return StaticLayoutCache[cacheKey] ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        StaticLayout.Builder.obtain(text, start, end, textPaint, width)
            .setAlignment(alignment)
            .setLineSpacing(spacingAdd, spacingMult)
            .setIncludePad(includePad)
            .setEllipsizedWidth(ellipsizedWidth)
            .setEllipsize(ellipsize)
            .build()
    } else {
        StaticLayout(
            text, start, end, textPaint, width, alignment,
            spacingMult, spacingAdd, includePad, ellipsize, ellipsizedWidth
        )
            .apply { StaticLayoutCache[cacheKey] = this }
    }
}

fun StaticLayout.draw(canvas: Canvas, x: Float, y: Float) {
    canvas.withTranslation(x, y) {
        draw(this)
    }
}

private object StaticLayoutCache {

    private const val MAX_SIZE = 50 // Arbitrary max number of cached items
    private val cache = lruCache<String, StaticLayout>(MAX_SIZE)

    operator fun set(key: String, staticLayout: StaticLayout) {
        cache.put(key, staticLayout)
    }

    operator fun get(key: String): StaticLayout? {
        return cache[key]
    }
}