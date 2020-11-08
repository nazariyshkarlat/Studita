package com.studita.domain.interactor.complete_chapter_part

import com.studita.domain.entity.CompleteExercisesRequestData
import com.studita.domain.entity.CompletedExercisesData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.CompleteExercisesStatus
import java.util.*

interface CompleteExercisesInteractor {

    suspend fun completeExercises(
        completeExercisesRequestData: CompleteExercisesRequestData,
        retryCount: Int = 3
    ): CompleteExercisesStatus

    suspend fun syncCompleteLocalExercises(userIdTokenData: UserIdTokenData) : Boolean

    suspend fun deleteLocalCompletedExercises(completedDateTime: Date)

    suspend fun clearLocalCompletedExercises()

    suspend fun addLocalCompletedExercises(completedExercisesData: CompletedExercisesData)

}