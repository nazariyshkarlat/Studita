package com.example.studita.data.entity.exercise

import com.example.studita.domain.entity.exercise.*
import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName
import java.io.IOException

data class ExercisesRawResponse(@SerializedName("exercises_start_screen")val exercisesStartScreenEntity: ExercisesStartScreenEntity,
                                @SerializedName("exercises_description")val exercisesDescriptionEntity: ExercisesDescriptionEntity?,
                                @SerializedName("exercises")val exercisesRaw: JsonArray)

data class ExercisesResponse(val exercisesStartScreenEntity: ExercisesStartScreenEntity,
                             val exercisesDescriptionEntity: ExercisesDescriptionEntity?,
                             val exercises: List<ExerciseArrayEntity>)

data class ExercisesStartScreenEntity(@SerializedName("title")val title: String,
                                      @SerializedName("subtitle")val subtitle: String)

data class ExercisesDescriptionEntity(@SerializedName("text_parts")val textParts: List<String>,
                                      @SerializedName("parts_to_inject")val partsToInject: List<String>)

fun ExercisesDescriptionEntity.toBusinessEntity() = ExercisesDescriptionData(textParts, partsToInject)
fun ExercisesStartScreenEntity.toBusinessEntity() = ExercisesStartScreenData(title, subtitle)
fun ExerciseArrayEntity.toBusinessEntity() = when (this) {
    is ExerciseArrayEntity.ExerciseEntity ->{
        when(val exerciseInfo = exerciseInfo){
            is ExerciseInfo.ExerciseType1Info -> ExerciseData.ExerciseDataExercise.ExerciseType1Data(
                exerciseNumber!!, exerciseInfo.title, exerciseInfo.subtitle, exerciseInfo.variants.map { it.toExerciseShapeData() }, exerciseAnswer)
            is ExerciseInfo.ExerciseType2And14Info -> ExerciseData.ExerciseDataExercise.ExerciseType2And14Data(
                exerciseNumber!!, exerciseInfo.title.toExerciseShapeData(), exerciseInfo.subtitle, exerciseInfo.variants, exerciseAnswer)
            is ExerciseInfo.ExerciseType3Info -> ExerciseData.ExerciseDataExercise.ExerciseType3Data(
                exerciseNumber!!, exerciseInfo.title.toExerciseCharacterData(), exerciseInfo.subtitle, exerciseInfo.variants.map { it.toExerciseCharacterData() }, exerciseAnswer)
            is ExerciseInfo.ExerciseType4Info -> ExerciseData.ExerciseDataExercise.ExerciseType4Data(exerciseNumber!!, exerciseInfo.title.toExerciseCharacterData(), exerciseInfo.subtitle, exerciseInfo.variants, exerciseAnswer)
            is ExerciseInfo.ExerciseType5And6And18Info -> ExerciseData.ExerciseDataExercise.ExerciseType5And6And18Data(exerciseNumber!!, exerciseInfo.title, exerciseInfo.subtitle, exerciseInfo.variants, exerciseAnswer)
            is ExerciseInfo.ExerciseType7Info -> ExerciseData.ExerciseDataExercise.ExerciseType7Data(exerciseNumber!!,exerciseInfo.title, exerciseAnswer)
            is ExerciseInfo.ExerciseType8And12Info -> ExerciseData.ExerciseDataExercise.ExerciseType8And12Data(exerciseNumber!!, exerciseInfo.title, exerciseInfo.subtitle, exerciseInfo.variants, exerciseAnswer)
            is ExerciseInfo.ExerciseType9Info -> ExerciseData.ExerciseDataExercise.ExerciseType9Data(exerciseNumber!!,exerciseInfo.title, exerciseAnswer)
            is ExerciseInfo.ExerciseType10Info -> ExerciseData.ExerciseDataExercise.ExerciseType10Data(exerciseNumber!!,exerciseInfo.titleParts, exerciseInfo.subtitle, exerciseAnswer, exerciseInfo.isNumeral)
            is ExerciseInfo.ExerciseType11Info -> ExerciseData.ExerciseDataExercise.ExerciseType11Data(exerciseNumber!!,exerciseInfo.titleParts, exerciseInfo.filter.toExerciseType11Filter(), exerciseInfo.compareNumber, exerciseAnswer)
            is ExerciseInfo.ExerciseType13Info -> ExerciseData.ExerciseDataExercise.ExerciseType13Data(exerciseNumber!!, exerciseInfo.title.toExerciseShapeEquation(), exerciseInfo.subtitle, exerciseInfo.variants, exerciseAnswer)
            is ExerciseInfo.ExerciseType15Info -> ExerciseData.ExerciseDataExercise.ExerciseType15Data(exerciseNumber!!, exerciseInfo.title, exerciseInfo.subtitle, exerciseInfo.variants, exerciseAnswer)
            is ExerciseInfo.ExerciseType16Info -> ExerciseData.ExerciseDataExercise.ExerciseType16Data(exerciseNumber!!, exerciseInfo.titleParts, exerciseInfo.subtitle, exerciseAnswer)
            is ExerciseInfo.ExerciseType17Info -> ExerciseData.ExerciseDataExercise.ExerciseType17Data(exerciseNumber!!, exerciseInfo.title.toExerciseShapeEquation(), exerciseInfo.subtitle, exerciseInfo.variants.map { it.toExerciseShapeData() }, exerciseAnswer)
        }
    }
    is ExerciseArrayEntity.ScreenEntity ->{
        when(val screenInfo = screenInfo){
            is ScreenInfo.ScreenType1Info -> ExerciseData.ExerciseDataScreen.ScreenType1Data(exerciseNumber, screenInfo.title, screenInfo.subtitle, screenInfo.partsToInject, screenInfo.image)
            is ScreenInfo.ScreenType2Info -> ExerciseData.ExerciseDataScreen.ScreenType2Data(exerciseNumber, screenInfo.title)
            is ScreenInfo.ScreenType3Info -> ExerciseData.ExerciseDataScreen.ScreenType3Data(exerciseNumber, screenInfo.title, screenInfo.subtitle, screenInfo.partsToInject)
        }
    }
}
fun List<String>.toExerciseShapeEquation(): List<ExerciseShapeEquationMemberData>{
    val resultList = ArrayList<ExerciseShapeEquationMemberData>()
    val shape = this[0]
    val equationParts = this[1].split(' ')

    equationParts.forEach{part->
        part.toIntOrNull()?.let { resultList.add(ExerciseShapeData(shape, it)) } ?: run{resultList.add(ExerciseOperatorData(part.first().toOperator()))}
    }
    return resultList
}
fun ExercisesResponse.toBusinessEntity() =  ExercisesResponseData(exercisesStartScreenEntity.toBusinessEntity(), exercisesDescriptionEntity?.toBusinessEntity(), exercises.map { it.toBusinessEntity() })
fun String.toExerciseType11Filter() = when(this){
    "bigger" -> ExerciseType11Filter.BIGGER
    "lower" -> ExerciseType11Filter.LOWER
    else -> throw IOException("unexpected exercise type 11 filter")
}
