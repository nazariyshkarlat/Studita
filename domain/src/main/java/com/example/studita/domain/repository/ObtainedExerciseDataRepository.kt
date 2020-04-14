package com.example.studita.domain.repository

import com.example.studita.domain.entity.ObtainedExerciseDataData
import com.example.studita.domain.entity.UserTokenIdData

interface ObtainedExerciseDataRepository {

    suspend fun saveObtainedExerciseData(userTokenIdData: UserTokenIdData, obtainedExerciseDataData: ObtainedExerciseDataData) : Int

}