package com.example.studita.data.entity.exercise

import com.google.gson.annotations.SerializedName

data class ExercisesRawResponse(@SerializedName("exercises_start_screen")val exercisesStartScreen: ExercisesStartScreen,
                                @SerializedName("exercises_description")val exercisesDescription: ExercisesDescription,
                                @SerializedName("exercises")val exercisesRaw: Any)

data class ExercisesResponse(val exercisesStartScreen: ExercisesStartScreen,
                             val exercisesDescription: ExercisesDescription,
                             val exercises: List<ExerciseEntity>)

data class ExercisesStartScreen(@SerializedName("title")val title: String,
                                @SerializedName("subtitle")val subtitle: String)

data class ExercisesDescription(@SerializedName("text_parts")val textParts: List<String>,
                                @SerializedName("parts_to_inject")val partsToInject: List<String>)