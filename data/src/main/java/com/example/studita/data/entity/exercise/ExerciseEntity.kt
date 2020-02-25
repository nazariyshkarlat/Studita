package com.example.studita.data.entity.exercise

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body

data class ExerciseEntity(@SerializedName("exercise_number")val exerciseNumber: Int, @SerializedName("exercise_info")val exerciseInfo: ExerciseInfo)