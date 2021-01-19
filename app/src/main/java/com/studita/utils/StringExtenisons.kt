package com.studita.utils

import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import androidx.annotation.FontRes
import androidx.core.text.buildSpannedString
import com.studita.presentation.views.CustomTypefaceSpan

fun String.createSpannableString(
    color: Int? = null,
    fontSize: Float? = null,
    typeFace: Typeface? = null
): SpannableString {
    val span = SpannableString(this)
    fontSize?.let {
        span.setSpan(AbsoluteSizeSpan(fontSize.spToPx()), 0, span.length, 0)
    }
    typeFace?.let {
        span.setSpan(
            CustomTypefaceSpan(
                " ",
                typeFace
            ), 0, this.length, 0
        )
    }
    color?.let {
        span.setSpan(ForegroundColorSpan(color), 0, span.length, 0)
    }

    return span
}
