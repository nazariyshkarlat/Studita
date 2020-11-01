package com.example.studita.data.entity

import com.example.studita.data.entity.exercise.ExerciseArrayEntity
import com.example.studita.domain.entity.ChapterPartData
import com.example.studita.domain.entity.InterestingData
import com.example.studita.domain.entity.LevelsDataData

data class OfflineDataEntity(val levelsData: String, val chaptersData: String, val exercisesData: String, val interestingData: String)