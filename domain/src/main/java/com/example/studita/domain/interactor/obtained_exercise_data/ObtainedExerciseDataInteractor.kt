package com.example.studita.domain.interactor.obtained_exercise_data

import com.example.studita.domain.entity.ObtainedExerciseDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.SaveObtainedExerciseDataStatus

interface ObtainedExerciseDataInteractor{

    suspend fun saveObtainedData(userIdTokenData: UserIdTokenData, obtainedExerciseDataData: ObtainedExerciseDataData): SaveObtainedExerciseDataStatus

}