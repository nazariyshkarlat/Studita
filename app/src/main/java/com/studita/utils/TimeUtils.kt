package com.studita.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.core.content.res.ResourcesCompat
import com.studita.R
import com.studita.utils.LanguageUtils.getResourcesRussianLocale
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToLong

object TimeUtils {

    private const val justNow = 30L
    private const val minuteInSeconds = 60L
    private const val hourInSeconds = 3600L
    private const val dayInSeconds = 86400L
    private const val weekInSeconds = 604800L
    private const val monthInSeconds = 2592000L
    private const val yearInSeconds = 31556952L

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy")

    fun getCalendarDayCount(startDateTime: Date, endDateTime: Date): Long {
        val dateStartStr = simpleDateFormat.format(startDateTime)
        val dateEndStr = simpleDateFormat.format(endDateTime)

        val dateStart = simpleDateFormat.parse(dateStartStr)!!
        val dateEnd = simpleDateFormat.parse(dateEndStr)!!

        return ((dateEnd.time - dateStart.time) / 86400000.toDouble()).roundToLong()
    }

    fun Long.secondsToAgoString(context: Context): String {
        return when {
            this < justNow -> context.resources.getString(R.string.just_now)
            this < minuteInSeconds -> "$this ${getResourcesRussianLocale(context)?.getQuantityString(
                R.plurals.seconds_ago_plurals,
                this.toInt()
            )}"
            this < hourInSeconds -> "${(this / minuteInSeconds).toInt()} ${getResourcesRussianLocale(
                context
            )?.getQuantityString(R.plurals.minutes_ago_plurals, (this / minuteInSeconds).toInt())}"
            this < dayInSeconds -> "${(this / hourInSeconds).toInt()} ${getResourcesRussianLocale(
                context
            )?.getQuantityString(R.plurals.hours_ago_plurals, (this / hourInSeconds).toInt())}"
            this < weekInSeconds -> "${(this / dayInSeconds).toInt()} ${getResourcesRussianLocale(
                context
            )?.getQuantityString(R.plurals.days_ago_plurals, (this / dayInSeconds).toInt())}"
            this < monthInSeconds -> "${(this / weekInSeconds).toInt()} ${getResourcesRussianLocale(
                context
            )?.getQuantityString(R.plurals.weeks_ago_plurals, (this / weekInSeconds).toInt())}"
            this < yearInSeconds -> "${(this / monthInSeconds).toInt()} ${getResourcesRussianLocale(
                context
            )?.getQuantityString(R.plurals.months_ago_plurals, (this / monthInSeconds).toInt())}"
            else -> "${(this / yearInSeconds).toInt()} ${getResourcesRussianLocale(context)?.getQuantityString(
                R.plurals.years_ago_plurals,
                (this / yearInSeconds).toInt()
            )}"
        }
    }

    fun getHours(timeInSeconds: Long) = timeInSeconds / hourInSeconds

    fun getMinutes(timeInSeconds: Long) =
        (((timeInSeconds % weekInSeconds) % dayInSeconds) % hourInSeconds) / minuteInSeconds

    fun getSeconds(timeInSeconds: Long) =
        (((timeInSeconds % weekInSeconds) % dayInSeconds) % hourInSeconds) % minuteInSeconds

    fun styleTimeText(context: Context, text: String): SpannableStringBuilder {
        val secondaryColor = ThemeUtils.getSecondaryColor(context)
        val builder = SpannableStringBuilder()
        val words = text.split(" ")
        val primaryWords = words.filterIndexed { index, _ -> index % 2 == 0 }.toTypedArray()
        val secondaryWords = words.filterIndexed { index, _ -> index % 2 != 0 }.toTypedArray()
        val secondarySpans = ArrayList<SpannableString>()
        for (secondaryWord in secondaryWords) {
            secondarySpans.add(
                secondaryWord.createSpannableString(
                    secondaryColor,
                    16F,
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
                "${hours.first} ${hours.second} ${if (minutes.first > 0) {
                    "${minutes.first} ${minutes.second}"
                } else
                    ""
                }"
            }
            minutes.first > 0 -> {
                "${minutes.first} ${minutes.second} ${if (seconds.first > 0) {
                    "${seconds.first} ${seconds.second}"
                } else
                    ""
                }"
            }
            else -> {
                "${seconds.first} ${seconds.second}"
            }
        }.trim()

    fun timeIsAutomatically(context: Context) = android.provider.Settings.Global.getInt(
        context.contentResolver,
        android.provider.Settings.Global.AUTO_TIME,
        0
    ) == 1
}