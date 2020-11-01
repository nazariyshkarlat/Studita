package com.example.studita.data.repository.datasource.interesting.result

import com.example.studita.data.entity.exercise.ExerciseReportEntity
import com.example.studita.data.entity.exercise.ExerciseReportRequest
import com.example.studita.data.entity.exercise.ExerciseRequestEntity
import com.example.studita.data.entity.exercise.ExerciseResponseEntity
import com.example.studita.data.entity.interesting.InterestingLikeRequest

interface InterestingResultDataStore {

    suspend fun sendInterestingLike(
        interestingLikeRequest: InterestingLikeRequest
    ) : Int
}