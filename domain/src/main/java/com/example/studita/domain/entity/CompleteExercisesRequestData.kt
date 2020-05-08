package com.example.studita.domain.entity

import java.util.*

data class CompletedExercisesData(val chapterNumber: Int,
                                  val chapterPartNumber: Int,
                                  val percent: Float,
                                  val datetime: Date,
                                  val obtainedTime: Long)

data class CompleteExercisesRequestData(val userIdToken: UserIdTokenData?, val completedExercisesEntity: CompletedExercisesData)