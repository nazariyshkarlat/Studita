package com.example.studita.data.entity.mapper.exercise

import android.graphics.drawable.shapes.Shape
import com.example.studita.data.entity.exercise.*
import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.exercise.*

class ExercisesDataMapper :
    Mapper<ExercisesResponse, ExercisesResponseData> {
    override fun map(source: ExercisesResponse): ExercisesResponseData {
        val exercises = source.exercises.map {
            when (it) {
                is ExerciseArrayEntity.ExerciseEntity ->{
                    when(val exerciseInfo = it.exerciseInfo){
                        is ExerciseInfo.ExerciseType1Info -> ExerciseData.ExerciseDataExercise.ExerciseType1Data(it.exerciseNumber, exerciseInfo.title, exerciseInfo.subtitle, ExerciseShapesMapper().map(exerciseInfo.variants))
                        is ExerciseInfo.ExerciseType2Info -> ExerciseData.ExerciseDataExercise.ExerciseType2Data(it.exerciseNumber, ExerciseShapeMapper().map(exerciseInfo.title), exerciseInfo.subtitle, exerciseInfo.variants)
                        is ExerciseInfo.ExerciseType3Info -> ExerciseData.ExerciseDataExercise.ExerciseType3Data(it.exerciseNumber, ExerciseNumberMapper().map(exerciseInfo.title), exerciseInfo.subtitle,ExerciseNumbersMapper().map(exerciseInfo.variants))
                        is ExerciseInfo.ExerciseType4Info -> ExerciseData.ExerciseDataExercise.ExerciseType4Data(it.exerciseNumber, ExerciseNumberMapper().map(exerciseInfo.title), exerciseInfo.subtitle, exerciseInfo.variants)
                    }
                }
                is ExerciseArrayEntity.ScreenEntity ->{
                    when(val screenInfo = it.screenInfo){
                        is ScreenInfo.ScreenType1Info -> ExerciseData.ExerciseDataScreen.ScreenType1Data(it.exerciseNumber, screenInfo.title, screenInfo.subtitle, screenInfo.partsToInject, screenInfo.image)
                        is ScreenInfo.ScreenType2Info -> ExerciseData.ExerciseDataScreen.ScreenType2Data(it.exerciseNumber, screenInfo.title)
                        is ScreenInfo.ScreenType3Info -> ExerciseData.ExerciseDataScreen.ScreenType3Data(it.exerciseNumber, screenInfo.title, screenInfo.subtitle, screenInfo.partsToInject)
                    }
                }
            }
        }
        return ExercisesResponseData(ExercisesStartScreenDataMapper().map(source.exercisesStartScreen), ExercisesDescriptionMapper().map(source.exercisesDescription), exercises)
    }
}

class ExercisesStartScreenDataMapper:
    Mapper<ExercisesStartScreen, ExercisesStartScreenData> {
    override fun map(source: ExercisesStartScreen): ExercisesStartScreenData
            = ExercisesStartScreenData(source.title, source.subtitle)
}

class ExercisesDescriptionMapper:
    Mapper<ExercisesDescription, ExercisesDescriptionData> {
    override fun map(source: ExercisesDescription): ExercisesDescriptionData
            = ExercisesDescriptionData(source.textParts, source.partsToInject)
}

class ExerciseShapesMapper: Mapper<List<List<String>>, List<ExerciseShapeData>> {
    override fun map(source: List<List<String>>): List<ExerciseShapeData>  = source.map { ExerciseShapeMapper().map(it) }
}

class ExerciseNumbersMapper: Mapper<List<List<String>>, List<ExerciseNumberData>> {
    override fun map(source: List<List<String>>): List<ExerciseNumberData>  = source.map { ExerciseNumberMapper().map(it) }
}

class ExerciseNumberMapper: Mapper<List<String>, ExerciseNumberData>{
    override fun map(source: List<String>): ExerciseNumberData = ExerciseNumberData(source[0], source[1].toInt())
}

class ExerciseShapeMapper: Mapper<List<String>, ExerciseShapeData> {
    override fun map(source: List<String>): ExerciseShapeData = ExerciseShapeData(source[0], source[1].toInt())
}