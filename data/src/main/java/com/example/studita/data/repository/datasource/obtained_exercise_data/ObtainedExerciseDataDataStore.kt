package com.example.studita.data.repository.datasource.obtained_exercise_data

import com.example.studita.data.entity.obtained_exercise_data.ObtainedExerciseDataEntity
import com.example.studita.data.entity.UserIdToken

interface ObtainedExerciseDataDataStore {

    suspend fun trySaveData(userIdToken: UserIdToken, obtainedExerciseDataEntity: ObtainedExerciseDataEntity): Int

}