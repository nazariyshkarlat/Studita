package com.example.studita.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class UserStatisticsEntity(@SerializedName("time_type")val timeType: String,
                                @SerializedName("obtained_XP")val obtainedXP: Int,
                                @SerializedName("obtained_time")val obtainedTime: Long,
                                @SerializedName("obtained_exercises")val obtainedExercises: Int,
                                @SerializedName("obtained_trainings")val obtainedTrainings: Int,
                                @SerializedName("obtained_achievements")val obtainedAchievements: Int)