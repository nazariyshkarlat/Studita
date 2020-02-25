package com.example.studita.presentation.model.mapper

import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.presentation.model.ExerciseUiModel

class ExercisesUiModelMapper : Mapper<List<ExerciseData>, List<ExerciseUiModel>> {

    override fun map(source: List<ExerciseData>): List<ExerciseUiModel> = source.map{
        when(it){
            is ExerciseData.ExerciseType1 -> ExerciseUiModel.ExerciseUi1(it.exerciseNumber, it.equation)
            is ExerciseData.ExerciseType2 -> ExerciseUiModel.ExerciseUi2(it.exerciseNumber, it.equation, it.variants)
            is ExerciseData.ExerciseType3 -> ExerciseUiModel.ExerciseUi3(it.exerciseNumber, it.equation)
            is ExerciseData.ExerciseType4 -> ExerciseUiModel.ExerciseUi4(it.exerciseNumber, it.expressionParts, it.expressionResult)
            is ExerciseData.ExerciseType5 -> ExerciseUiModel.ExerciseUi5(it.exerciseNumber, it.expressionParts, it.expressionResult, it.variants)
            is ExerciseData.ExerciseType6 -> ExerciseUiModel.ExerciseUi6(it.exerciseNumber, it.result)
            is ExerciseData.ExerciseType7 -> ExerciseUiModel.ExerciseUi7(it.exerciseNumber, it.expressionParts, it.expressionResult, it.variants)
            is ExerciseData.ExerciseType8 -> ExerciseUiModel.ExerciseUi8(it.exerciseNumber, it.desiredShape, it.shapes)
            is ExerciseData.ExerciseType9 -> ExerciseUiModel.ExerciseUi9(it.exerciseNumber, it.desiredType, it.numbers)
        }
    }

}