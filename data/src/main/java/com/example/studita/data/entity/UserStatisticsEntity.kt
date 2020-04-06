package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class UserStatisticsEntity(@SerializedName("obtained_XP")val obtainedXP: Int,
                                @SerializedName("obtained_time")val obtained_time: Int,
                                @SerializedName("obtained_lessons")val obtained_lessons: Int,
                                @SerializedName("obtained_trainings")val obtained_trainings: Int,
                                @SerializedName("obtained_achievements")val obtained_achievements: Int)