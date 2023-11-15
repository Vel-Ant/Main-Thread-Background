package ru.netology.nmedia.dto

import android.content.Context
import ru.netology.nmedia.R

enum class DateSeparator {
    TODAY,
    YESTERDAY,
    TWO_DAYS_AGO,
    WEEK_AGO,
    TWO_WEEKS_AGO,
    ;
}

fun DateSeparator.getText(context: Context): String = context.getString (
    when (this) {
        DateSeparator.TODAY -> R.string.today
        DateSeparator.YESTERDAY -> R.string.yesterday
        DateSeparator.TWO_DAYS_AGO -> R.string.two_days_ago
        DateSeparator.WEEK_AGO -> R.string.week_ago
        DateSeparator.TWO_WEEKS_AGO -> R.string.two_weeks_ago
    }
)