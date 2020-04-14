package com.example.studita.data.repository.datasource.obtained_exercise_data

import com.example.studita.data.entity.obtained_exercise_data.ObtainedExerciseDataEntity
import com.example.studita.data.entity.UserTokenId

interface ObtainedExerciseDataDataStore {

    suspend fun trySaveData(userTokenId: UserTokenId, obtainedExerciseDataEntity: ObtainedExerciseDataEntity): Int

}