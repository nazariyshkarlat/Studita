package com.example.studita.data.entity.mapper.exercise

import com.example.studita.data.entity.AuthorizationRequestEntity
import com.example.studita.data.entity.exercise.ExerciseRequestEntity
import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.exercise.ExerciseRequestData

class ExerciseRequestDataMapper : Mapper<ExerciseRequestData, ExerciseRequestEntity> {


    override fun map(source: ExerciseRequestData): ExerciseRequestEntity {
        return ExerciseRequestEntity(
            source.exerciseAnswer
        )
    }
}