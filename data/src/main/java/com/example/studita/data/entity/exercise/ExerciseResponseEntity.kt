package com.example.studita.data.entity.exercise

import com.google.gson.annotations.SerializedName

data class ExerciseResponseEntity(@SerializedName("result")val exerciseResult: Boolean, @SerializedName("description")val description: String?)