package com.example.studita.domain.repository

import com.example.studita.domain.entity.ObtainedExerciseDataData
import com.example.studita.domain.entity.UserIdTokenData

interface ObtainedExerciseDataRepository {

    suspend fun saveObtainedExerciseData(userIdTokenData: UserIdTokenData, obtainedExerciseDataData: ObtainedExerciseDataData) : Int

}