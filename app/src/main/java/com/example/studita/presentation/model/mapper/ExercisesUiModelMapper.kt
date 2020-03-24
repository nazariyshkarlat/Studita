package com.example.studita.presentation.model.mapper

import androidx.annotation.IdRes
import com.example.studita.R
import com.example.studita.data.entity.exercise.*
import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.data.entity.mapper.exercise.ExerciseShapeMapper
import com.example.studita.data.entity.mapper.exercise.ExerciseShapesMapper
import com.example.studita.domain.entity.exercise.*
import com.example.studita.presentation.model.ExerciseShape
import com.example.studita.presentation.model.ExerciseUiModel
import java.io.IOException

class ExercisesUiModelMapper : Mapper<List<ExerciseData>, List<ExerciseUiModel>> {

    override fun map(source: List<ExerciseData>):  List<ExerciseUiModel> {
        return source.map {
            when (it) {
                is ExerciseData.ExerciseDataExercise.ExerciseType1Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel(it.exerciseNumber, it.title, it.subtitle, ExerciseUiShapesMapper().map(it.variants))
                is ExerciseData.ExerciseDataExercise.ExerciseType2Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel(it.exerciseNumber, ExerciseUiShapeMapper().map(it.title), it.subtitle, it.variants)
                is ExerciseData.ExerciseDataExercise.ExerciseType3Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel(it.exerciseNumber, it.title, it.subtitle, it.variants)
                is ExerciseData.ExerciseDataExercise.ExerciseType4Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel(it.exerciseNumber, it.title, it.subtitle, it.variants)
                is ExerciseData.ExerciseDataExercise.ExerciseType5and6Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType5and6UiModel(it.exerciseNumber, it.title, it.subtitle, it.variants)
                is ExerciseData.ExerciseDataExercise.ExerciseType7Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType7UiModel(it.exerciseNumber,it.title)
                is ExerciseData.ExerciseDataExercise.ExerciseType8Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8UiModel(it.exerciseNumber, it.title, it.subtitle, it.variants)
                is ExerciseData.ExerciseDataExercise.ExerciseType9Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel(it.exerciseNumber,it.title)
                is ExerciseData.ExerciseDataExercise.ExerciseType10Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType10UiModel(it.exerciseNumber,it.titleParts, it.subtitle)
                is ExerciseData.ExerciseDataExercise.ExerciseType11Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType11UiModel(it.exerciseNumber,it.titleParts, it.filter, it.compareNumber)

                is ExerciseData.ExerciseDataScreen.ScreenType1Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType1UiModel(it.exerciseNumber, it.title, it.subtitle, it.partsToInject, getShapeByName(it.image))
                is ExerciseData.ExerciseDataScreen.ScreenType2Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType2UiModel(it.exerciseNumber, it.title)
                is ExerciseData.ExerciseDataScreen.ScreenType3Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel(it.exerciseNumber, it.title, it.subtitle, it.partsToInject)
            }
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