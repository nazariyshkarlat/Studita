package com.example.studita.domain.interactor.obtained_exercise_data

import com.example.studita.domain.entity.ObtainedExerciseDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.SaveObtainedExerciseDataStatus
import com.example.studita.domain.repository.ObtainedExerciseDataRepository

class ObtainedExerciseDataInteractorImpl(private val repository: ObtainedExerciseDataRepository) :
    ObtainedExerciseDataInteractor {

    override suspend fun saveObtainedData(
        userIdTokenData: UserIdTokenData,
        obtainedExerciseDataData: ObtainedExerciseDataData
    ): SaveObtainedExerciseDataStatus =
        try {
            when (repository.saveObtainedExerciseData(userIdTokenData, obtainedExerciseDataData)) {
                200 -> SaveObtainedExerciseDataStatus.Success
                404 -> SaveObtainedExerciseDataStatus.Failure
                else -> SaveObtainedExerciseDataStatus.ServiceUnavailable
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                SaveObtainedExerciseDataStatus.NoConnection
            else
                SaveObtainedExerciseDataStatus.ServiceUnavailable
        }


}