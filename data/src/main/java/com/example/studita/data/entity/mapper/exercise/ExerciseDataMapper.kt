package com.example.studita.data.entity.mapper.exercise

import com.example.studita.data.entity.exercise.ExerciseEntity
import com.example.studita.data.entity.exercise.ExerciseInfo
import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.exercise.ExerciseData

class ExerciseDataMapper :
    Mapper<List<ExerciseEntity>, List<ExerciseData>> {
    override fun map(source: List<ExerciseEntity>): List<ExerciseData>
            = source.map{
        when(it.exerciseInfo) {
            is ExerciseInfo.ExerciseType1Info -> ExerciseData.ExerciseType1(it.exerciseNumber, it.exerciseInfo.equation)
            is ExerciseInfo.ExerciseType2Info -> ExerciseData.ExerciseType2(it.exerciseNumber, it.exerciseInfo.equation, it.exerciseInfo.variants)
            is ExerciseInfo.ExerciseType3Info -> ExerciseData.ExerciseType3(it.exerciseNumber, it.exerciseInfo.equation)
            is ExerciseInfo.ExerciseType4Info -> ExerciseData.ExerciseType4(it.exerciseNumber, it.exerciseInfo.expressionParts, it.exerciseInfo.expressionResult)
            is ExerciseInfo.ExerciseType5Info -> ExerciseData.ExerciseType5(it.exerciseNumber, it.exerciseInfo.expressionParts, it.exerciseInfo.expressionResult, it.exerciseInfo.variants)
            is ExerciseInfo.ExerciseType6Info -> ExerciseData.ExerciseType6(it.exerciseNumber, it.exerciseInfo.result)
            is ExerciseInfo.ExerciseType7Info -> ExerciseData.ExerciseType7(it.exerciseNumber, it.exerciseInfo.expressionParts, it.exerciseInfo.expressionResult, it.exerciseInfo.variants)
            is ExerciseInfo.ExerciseType8Info -> ExerciseData.ExerciseType8(it.exerciseNumber, it.exerciseInfo.desiredShape, it.exerciseInfo.shapes)
            is ExerciseInfo.ExerciseType9Info -> ExerciseData.ExerciseType9(it.exerciseNumber, it.exerciseInfo.desiredType, it.exerciseInfo.numbers)
        }
    }
}