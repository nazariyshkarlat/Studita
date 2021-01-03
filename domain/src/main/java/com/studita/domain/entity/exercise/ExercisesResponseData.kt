package com.studita.domain.entity.exercise

import kotlinx.serialization.Serializable

@Serializable
data class ExercisesResponseData(
    val exercisesStartScreen: ExercisesStartScreenData,
    val exercisesDescription: ExercisesDescriptionData?,
    val exercises: List<ExerciseData>
)

@Serializable
data class ExercisesStartScreenData(
    val title: String,
    val subtitle: String
)

@Serializable
data class ExercisesDescriptionData(
    val textParts: List<String>,
    val partsToInject: List<String>?
)
