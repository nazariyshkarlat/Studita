package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.ObtainedExerciseDataMapper
import com.example.studita.data.entity.mapper.UserIdTokenMapper
import com.example.studita.data.repository.datasource.obtained_exercise_data.ObtainedExerciseDataDataStoreFactory
import com.example.studita.domain.entity.ObtainedExerciseDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.repository.ObtainedExerciseDataRepository

class ObtainedExerciseDataRepositoryImpl(private val obtainedExerciseDataDataStoreFactory: ObtainedExerciseDataDataStoreFactory, private val userIdTokenMapper: UserIdTokenMapper) : ObtainedExerciseDataRepository{
    override suspend fun saveObtainedExerciseData(
        userIdTokenData: UserIdTokenData,
        obtainedExerciseDataData: ObtainedExerciseDataData
    ): Int {
        return obtainedExerciseDataDataStoreFactory.create().trySaveData(userIdTokenMapper.map(userIdTokenData), ObtainedExerciseDataMapper().map(obtainedExerciseDataData))
    }

}