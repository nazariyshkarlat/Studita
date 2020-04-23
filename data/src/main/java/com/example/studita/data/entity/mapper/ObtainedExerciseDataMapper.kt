package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.obtained_exercise_data.ObtainedExerciseDataEntity
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.entity.ObtainedExerciseDataData

class ObtainedExerciseDataMapper : Mapper<ObtainedExerciseDataData, ObtainedExerciseDataEntity>{
    override fun map(source: ObtainedExerciseDataData): ObtainedExerciseDataEntity =
        ObtainedExerciseDataEntity(
            source.newLevelsCount,
            source.training,
            source.newLevelXP,
            source.obtainedXP,
            source.obtainedTime,
            source.chapterNumber
        )
}