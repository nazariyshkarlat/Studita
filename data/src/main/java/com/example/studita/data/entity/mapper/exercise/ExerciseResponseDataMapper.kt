package com.example.studita.data.entity.mapper.exercise

import com.example.studita.data.entity.exercise.ExerciseResponseEntity
import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.exercise.ExerciseResponseData

class ExerciseResponseDataMapper : Mapper<ExerciseResponseEntity, ExerciseResponseData> {

    override fun map(source: ExerciseResponseEntity): ExerciseResponseData {
        return ExerciseResponseData(
            source.exerciseResult,
            source.description
        )
    }
}