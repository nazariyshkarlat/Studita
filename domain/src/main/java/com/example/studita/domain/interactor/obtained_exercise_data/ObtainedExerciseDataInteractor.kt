package com.example.studita.domain.interactor.obtained_exercise_data

import com.example.studita.domain.entity.ObtainedExerciseDataData
import com.example.studita.domain.entity.UserTokenIdData
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.interactor.LogInStatus
import com.example.studita.domain.interactor.SaveObtainedExerciseDataStatus
import com.example.studita.domain.interactor.SignInWithGoogleStatus
import com.example.studita.domain.interactor.SignUpStatus

interface ObtainedExerciseDataInteractor{

    suspend fun saveObtainedData(userTokenIdData: UserTokenIdData, obtainedExerciseDataData: ObtainedExerciseDataData): SaveObtainedExerciseDataStatus

}