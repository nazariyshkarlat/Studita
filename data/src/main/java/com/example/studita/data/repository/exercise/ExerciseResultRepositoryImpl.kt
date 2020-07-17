package com.example.studita.data.repository.exercise

import com.example.studita.data.entity.exercise.toBusinessEntity
import com.example.studita.data.repository.datasource.exercises.result.ExerciseResultDataStoreFactory
import com.example.studita.domain.entity.exercise.*
import com.example.studita.domain.repository.ExerciseResultRepository

class ExerciseResultRepositoryImpl(private val exerciseResultDataStoreFactory: ExerciseResultDataStoreFactory):
    ExerciseResultRepository {

    override suspend fun getExerciseResult(
        exerciseNumber: Int,
        exerciseRequestData: ExerciseRequestData
    ): Pair<Int, ExerciseResponseData> =
        with(exerciseResultDataStoreFactory.create().getExerciseResult(exerciseNumber, exerciseRequestData.toBusinessEntity())){
            this.first to this.second.toBusinessEntity()
        }

    override suspend fun formExerciseResponse(
        exerciseData: ExerciseData.ExerciseDataExercise,
        exerciseRequestData: ExerciseRequestData
    ): ExerciseResponseData {
        return ExerciseResponseData(exerciseData.exerciseAnswer == exerciseRequestData.exerciseAnswer,
            ExerciseResponseDescriptionData(when(exerciseData){
                is ExerciseData.ExerciseDataExercise.ExerciseType1Data -> {
                    val correctAnswer =
                        exerciseData.variants.first { it.count == exerciseData.exerciseAnswer!!.toInt() }
                    ExerciseResponseDescriptionContentData.DescriptionContentArray(correctAnswer)
                }
                is ExerciseData.ExerciseDataExercise.ExerciseType3Data -> {
                     ExerciseResponseDescriptionContentData.DescriptionContentString(exerciseData.variants.first{it.character.toString() == exerciseData.exerciseAnswer}.characterName)
                }
                else -> ExerciseResponseDescriptionContentData.DescriptionContentString(exerciseData.exerciseAnswer!!)
            }))
    }

}