package com.example.studita.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.core.content.res.ResourcesCompat
import com.example.studita.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.math.roundToLong

object TimeUtils {

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy")

    fun getCalendarDayCount(startDateTime: Date, endDateTime: Date): Long {
        val dateStartStr = simpleDateFormat.format(startDateTime)
        val dateEndStr = simpleDateFormat.format(endDateTime)

        val dateStart = simpleDateFormat.parse(dateStartStr)
        val dateEnd = simpleDateFormat.parse(dateEndStr)

        return ((dateEnd.time - dateStart.time) / 86400000.toDouble()).roundToLong()
    }

    fun getHours(timeInSeconds: Long) = timeInSeconds/3600

    fun getMinutes(timeInSeconds: Long) = (((timeInSeconds % 604800) % 86400) % 3600) / 60

    fun getSeconds(timeInSeconds: Long) = (((timeInSeconds % 604800) % 86400) % 3600) % 60

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
        hours: Pair<Long, String>,
        minutes: Pair<Long, String>,
        seconds: Pair<Long, String>
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
        }.trim()
}