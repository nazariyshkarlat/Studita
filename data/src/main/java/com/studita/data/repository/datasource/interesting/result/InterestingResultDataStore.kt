package com.studita.data.repository.datasource.interesting.result

import com.studita.data.entity.exercise.ExerciseReportEntity
import com.studita.data.entity.exercise.ExerciseReportRequest
import com.studita.data.entity.exercise.ExerciseRequestEntity
import com.studita.data.entity.exercise.ExerciseResponseEntity
import com.studita.data.entity.interesting.InterestingLikeRequest

interface InterestingResultDataStore {

    suspend fun sendInterestingLike(
        interestingLikeRequest: InterestingLikeRequest
    ) : Int
}