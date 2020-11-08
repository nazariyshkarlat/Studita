package com.studita.data.repository.exercise

import com.studita.data.entity.exercise.toBusinessEntity
import com.studita.data.entity.exercise.toRawEntity
import com.studita.data.repository.datasource.exercises.result.ExerciseResultDataStoreFactory
import com.studita.domain.entity.exercise.*
import com.studita.domain.repository.ExerciseResultRepository

class ExerciseResultRepositoryImpl(private val exerciseResultDataStoreFactory: ExerciseResultDataStoreFactory) :
    ExerciseResultRepository {

    override suspend fun getExerciseResult(
        exerciseNumber: Int,
        exerciseRequestData: ExerciseRequestData
    ): Pair<Int, ExerciseResponseData> =
        with(
            exerciseResultDataStoreFactory.create()
                .getExerciseResult(exerciseNumber, exerciseRequestData.toBusinessEntity())
        ) {
            this.first to this.second.toBusinessEntity()
        }

    override suspend fun formExerciseResponse(
        exerciseData: ExerciseData.ExerciseDataExercise,
        exerciseRequestData: ExerciseRequestData
    ) = ExerciseResponseData(
        exerciseData.exerciseAnswer!!.split(",")
            .toSet() == exerciseRequestData.exerciseAnswer.split(",").toSet(),
    ExerciseResponseDescriptionData(when (exerciseData) {
            is ExerciseData.ExerciseDataExercise.ExerciseType1Data -> {
                val correctAnswer =
                    exerciseData.variants.first { it.count == exerciseData.exerciseAnswer!!.toInt() }
                ExerciseResponseDescriptionContentData.DescriptionContentArray(correctAnswer)
            }
            is ExerciseData.ExerciseDataExercise.ExerciseType17Data -> {
                   val correctAnswer =
                       exerciseData.variants.first { it.count == exerciseData.exerciseAnswer!!.toInt() }
                   ExerciseResponseDescriptionContentData.DescriptionContentArray(correctAnswer)
            }
            else -> ExerciseResponseDescriptionContentData.DescriptionContentString(
                exerciseData.exerciseAnswer!!
            )
        }
        )
    )

    override suspend fun sendExerciseReport(exerciseReportRequestData: ExerciseReportRequestData): Int {
        return exerciseResultDataStoreFactory.create().sendExerciseReport(exerciseReportRequestData.toRawEntity())
    }

}