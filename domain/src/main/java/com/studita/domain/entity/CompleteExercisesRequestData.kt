package com.studita.domain.entity

import com.studita.domain.entity.serializer.DateSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CompletedExercisesData(
    val chapterNumber: Int,
    val chapterPartNumber: Int,
    val percent: Float,
    @Serializable(with = DateSerializer::class)
    val datetime: Date,
    val obtainedTime: Long,
    val exercisesBonusCorrectCount: Int
)

@Serializable
data class CompleteExercisesRequestData(
    val userIdToken: UserIdTokenData?,
    val completedExercisesData: CompletedExercisesData
)