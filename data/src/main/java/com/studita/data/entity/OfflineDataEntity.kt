package com.studita.data.entity

import com.studita.data.entity.exercise.ExerciseArrayEntity
import com.studita.domain.entity.ChapterPartData
import com.studita.domain.entity.InterestingData
import com.studita.domain.entity.LevelsDataData

data class OfflineDataEntity(val levelsData: String, val chaptersData: String, val exercisesData: String, val interestingData: String)