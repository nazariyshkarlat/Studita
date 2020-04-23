package com.example.studita.domain.entity

import java.util.*

data class ObtainedExerciseDataData(val datetime: Date,
                                    val newLevelsCount: Int,
                                    val training: Boolean,
                                    val newLevelXP: Int,
                                    val obtainedXP: Int,
                                    val obtainedTime: Long,
                                    val chapterNumber: Int)