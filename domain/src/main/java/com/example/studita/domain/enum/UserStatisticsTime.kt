package com.example.studita.domain.enum

enum class UserStatisticsTime{
    TODAY,
    YESTERDAY,
    WEEK,
    MONTH
}

fun UserStatisticsTime.timeToString() =  when(this){
    UserStatisticsTime.TODAY -> "today"
    UserStatisticsTime.YESTERDAY -> "yesterday"
    UserStatisticsTime.WEEK -> "week"
    UserStatisticsTime.MONTH -> "month"
}