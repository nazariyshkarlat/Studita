package com.example.studita.domain.interactor.obtained_exercise_data

import com.example.studita.domain.entity.ObtainedExerciseDataData
import com.example.studita.domain.entity.UserTokenIdData
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.LogInStatus
import com.example.studita.domain.interactor.SaveObtainedExerciseDataStatus
import com.example.studita.domain.interactor.SignInWithGoogleStatus
import com.example.studita.domain.interactor.SignUpStatus
import com.example.studita.domain.interactor.authorization.AuthorizationInteractor
import com.example.studita.domain.repository.AuthorizationRepository
import com.example.studita.domain.repository.ObtainedExerciseDataRepository

class ObtainedExerciseDataInteractorImpl(private val repository: ObtainedExerciseDataRepository) :
    ObtainedExerciseDataInteractor {

    override suspend fun saveObtainedData(
        userTokenIdData: UserTokenIdData,
        obtainedExerciseDataData: ObtainedExerciseDataData
    ): SaveObtainedExerciseDataStatus =
        try {
            when (repository.saveObtainedExerciseData(userTokenIdData, obtainedExerciseDataData)) {
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