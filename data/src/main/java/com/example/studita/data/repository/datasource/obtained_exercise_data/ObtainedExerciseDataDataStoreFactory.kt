package com.example.studita.data.repository.datasource.obtained_exercise_data

import com.example.studita.data.repository.datasource.authorization.AuthorizationDataStore
import com.example.studita.data.repository.datasource.authorization.CloudAuthorizationDataStore

class ObtainedExerciseDataDataStoreFactoryImpl(
    private val cloudObtainedExerciseDataDataStore: CloudObtainedExerciseDataDataStore
) : ObtainedExerciseDataDataStoreFactory {

    override fun create(priority: ObtainedExerciseDataDataStoreFactory.Priority) =
        if (priority == ObtainedExerciseDataDataStoreFactory.Priority.CLOUD)
            cloudObtainedExerciseDataDataStore
        else
            cloudObtainedExerciseDataDataStore
}

interface ObtainedExerciseDataDataStoreFactory{

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun  create(priority: Priority): ObtainedExerciseDataDataStore
}