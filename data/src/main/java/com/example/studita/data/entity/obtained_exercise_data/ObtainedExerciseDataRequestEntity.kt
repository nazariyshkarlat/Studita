package com.example.studita.data.entity.obtained_exercise_data

import com.example.studita.data.entity.UserIdToken
import com.google.gson.annotations.SerializedName

data class ObtainedExerciseDataRequestEntity(@SerializedName("auth_data")val userIdToken: UserIdToken, @SerializedName("exercise_data")val obtainedExerciseDataEntity: ObtainedExerciseDataEntity)