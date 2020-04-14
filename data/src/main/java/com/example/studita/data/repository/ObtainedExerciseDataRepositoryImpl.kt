package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.ObtainedExerciseDataMapper
import com.example.studita.data.entity.mapper.UserTokenIdMapper
import com.example.studita.data.repository.datasource.authorization.AuthorizationDataStoreFactory
import com.example.studita.data.repository.datasource.obtained_exercise_data.ObtainedExerciseDataDataStoreFactory
import com.example.studita.domain.entity.ObtainedExerciseDataData
import com.example.studita.domain.entity.UserTokenIdData
import com.example.studita.domain.repository.ObtainedExerciseDataRepository

class ObtainedExerciseDataRepositoryImpl(private val obtainedExerciseDataDataStoreFactory: ObtainedExerciseDataDataStoreFactory) : ObtainedExerciseDataRepository{
    override suspend fun saveObtainedExerciseData(
        userTokenIdData: UserTokenIdData,
        obtainedExerciseDataData: ObtainedExerciseDataData
    ): Int {
        return obtainedExerciseDataDataStoreFactory.create(ObtainedExerciseDataDataStoreFactory.Priority.CLOUD).trySaveData(UserTokenIdMapper().map(userTokenIdData), ObtainedExerciseDataMapper().map(obtainedExerciseDataData))
    }

}