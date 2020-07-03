package com.example.studita.presentation.model

sealed class ExerciseResultAnimation(open val to: Float) {

    data class ObtainedXP(override val to: Float) : ExerciseResultAnimation(to)
    data class LevelUPBonus(override val to: Float) : ExerciseResultAnimation(to)
    data class AllCorrectBonus(override val to: Float): ExerciseResultAnimation(to)
    data class SequenceBonus(override val to: Float): ExerciseResultAnimation(to)

}