package com.studita.data.entity.exercise

import com.google.gson.annotations.SerializedName

data class ExerciseRequestEntity(@SerializedName("answer") val exerciseAnswer: String)