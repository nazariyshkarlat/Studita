package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class UserStatisticsEntity(@SerializedName("obtained_XP")val obtainedXP: Int,
                                @SerializedName("obtained_time")val obtainedTime: Long,
                                @SerializedName("obtained_exercises")val obtainedExercises: Int,
                                @SerializedName("obtained_trainings")val obtainedTrainings: Int,
                                @SerializedName("obtained_achievements")val obtainedAchievements: Int)