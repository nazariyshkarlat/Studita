package com.example.studita.data.entity.obtained_exercise_data

import com.example.studita.data.entity.UserTokenId
import com.google.gson.annotations.SerializedName

data class ObtainedExerciseDataRequestEntity(@SerializedName("auth_data")val userTokenId: UserTokenId, @SerializedName("exercise_data")val obtainedExerciseDataEntity: ObtainedExerciseDataEntity)