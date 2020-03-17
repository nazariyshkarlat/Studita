package com.example.studita.domain.entity.exercise

data class ExercisesResponseData(val exercisesStartScreen: ExercisesStartScreenData,
                                 val exercisesDescription: ExercisesDescriptionData,
                                 val exercises: List<ExerciseData>)

data class ExercisesStartScreenData(val title: String,
                                val subtitle: String)

data class ExercisesDescriptionData(val textParts: List<String>,
                                val partsToInject: List<String>?)