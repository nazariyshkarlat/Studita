package com.example.studita.presentation.utils

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.core.content.res.ResourcesCompat
import com.example.studita.R

object TimeUtils {
    fun getHours(timeInSeconds: Int) = ((timeInSeconds % 604800) % 86400) / 3600

    fun getMinutes(timeInSeconds: Int) = (((timeInSeconds % 604800) % 86400) % 3600) / 60

    fun getSeconds(timeInSeconds: Int) = (((timeInSeconds % 604800) % 86400) % 3600) % 60

    fun styleTimeText(context: Context, text: String): SpannableStringBuilder {
        val secondaryColor = ColorUtils.getSecondaryColor(context)
        val builder = SpannableStringBuilder()
        val words = text.split(" ")
        val primaryWords = words.filterIndexed { index, _ -> index % 2 == 0 }.toTypedArray()
        val secondaryWords = words.filterIndexed { index, _ -> index % 2 != 0 }.toTypedArray()
        val secondarySpans = ArrayList<SpannableString>()
        for (secondaryWord in secondaryWords) {
            secondarySpans.add(
                secondaryWord.createSpannableString(
                    secondaryColor,
                    16,
                    ResourcesCompat.getFont(context, R.font.roboto_regular)
                )
            )
        }
        primaryWords.forEachIndexed { index, primaryWord ->
            builder.append(primaryWord)
            builder.append(" ")
            builder.append(secondarySpans[index])
            builder.append(" ")
        }
        return builder
    }

    fun getTimeText(
        hours: Pair<Int, String>,
        minutes: Pair<Int, String>,
        seconds: Pair<Int, String>
    ): String =
        when {
            hours.first > 0 -> {
                "$hours ${hours.second} ${if (seconds.first > 0) {
                    "${(if (minutes.first < 10) "0" else "")}${minutes.first} ${minutes.second}"
                } else
                    ""
                }"
            }
            minutes.first > 0 -> {
                "${minutes.first} ${minutes.second} ${if (seconds.first > 0) {
                    "${(if (seconds.first < 10) "0" else "")}${seconds.first} ${seconds.second}"
                } else
                    ""
                }"
            }
            else -> {
                "${seconds.first} ${seconds.second}"
            }
        }
}