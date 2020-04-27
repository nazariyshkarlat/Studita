package com.example.studita.presentation.model.mapper

import androidx.annotation.IdRes
import com.example.studita.R
import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.exercise.*
import com.example.studita.presentation.model.ExerciseShape
import com.example.studita.presentation.model.ExerciseUiModel
import java.io.IOException

class ExercisesUiModelMapper : Mapper<List<com.example.studita.domain.entity.exercise.ExerciseData>, List<ExerciseUiModel>> {

    override fun map(source: List<com.example.studita.domain.entity.exercise.ExerciseData>):  List<ExerciseUiModel> {
        return source.map {
            mapExerciseData(it)
        }
    }

    fun mapExerciseData(exerciseData: com.example.studita.domain.entity.exercise.ExerciseData): ExerciseUiModel {
        return when (exerciseData) {
            is ExerciseData.ExerciseDataExercise.ExerciseType1Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel(exerciseData.exerciseNumber, exerciseData.title, exerciseData.subtitle, ExerciseUiShapesMapper().map(exerciseData.variants))
            is ExerciseData.ExerciseDataExercise.ExerciseType2Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel(exerciseData.exerciseNumber, ExerciseUiShapeMapper().map(exerciseData.title), exerciseData.subtitle, exerciseData.variants)
            is ExerciseData.ExerciseDataExercise.ExerciseType3Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel(exerciseData.exerciseNumber, exerciseData.title, exerciseData.subtitle, exerciseData.variants)
            is ExerciseData.ExerciseDataExercise.ExerciseType4Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel(exerciseData.exerciseNumber, exerciseData.title, exerciseData.subtitle, exerciseData.variants)
            is ExerciseData.ExerciseDataExercise.ExerciseType5and6Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType5And6UiModel(exerciseData.exerciseNumber, exerciseData.title, exerciseData.subtitle, exerciseData.variants)
            is ExerciseData.ExerciseDataExercise.ExerciseType7Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType7UiModel(exerciseData.exerciseNumber,exerciseData.title)
            is ExerciseData.ExerciseDataExercise.ExerciseType8Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8UiModel(exerciseData.exerciseNumber, exerciseData.title, exerciseData.subtitle, exerciseData.variants)
            is ExerciseData.ExerciseDataExercise.ExerciseType9Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel(exerciseData.exerciseNumber,exerciseData.title)
            is ExerciseData.ExerciseDataExercise.ExerciseType10Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType10UiModel(exerciseData.exerciseNumber,exerciseData.titleParts, exerciseData.subtitle)
            is ExerciseData.ExerciseDataExercise.ExerciseType11Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType11UiModel(exerciseData.exerciseNumber,exerciseData.titleParts, exerciseData.filter, exerciseData.compareNumber)

            is ExerciseData.ExerciseDataScreen.ScreenType1Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType1UiModel(exerciseData.exerciseNumber, exerciseData.title, exerciseData.subtitle, exerciseData.partsToInject, getShapeByName(exerciseData.image))
            is ExerciseData.ExerciseDataScreen.ScreenType2Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType2UiModel(exerciseData.exerciseNumber, exerciseData.title)
            is ExerciseData.ExerciseDataScreen.ScreenType3Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel(exerciseData.exerciseNumber, exerciseData.title, exerciseData.subtitle, exerciseData.partsToInject)
        }
    }
}

class ExerciseUiShapesMapper: Mapper<List<ExerciseShapeData>, List<ExerciseShape>> {
    override fun map(source: List<ExerciseShapeData>): List<ExerciseShape>  = source.map { ExerciseUiShapeMapper().map(it) }
}

class ExerciseUiShapeMapper: Mapper<ExerciseShapeData, ExerciseShape> {
    override fun map(source: ExerciseShapeData): ExerciseShape = ExerciseShape(getShapeByName(source.shape), source.count)
}

@IdRes
fun getShapeByName(name: String) : Int = when(name){
    "rect" -> R.drawable.exercise_rectangle
    else -> throw IOException("Unexpected shape name")
}