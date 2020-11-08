package com.studita.data.repository.datasource.exercises

class ExercisesDataStoreFactoryImpl(
    private val cloudExercisesJsonDataStore: CloudExercisesJsonDataStore,
    private val diskExercisesJsonDataStore: DiskExercisesJsonDataStore
) : ExercisesDataStoreFactory {

    override fun create(priority: ExercisesDataStoreFactory.Priority) =
        if (priority == ExercisesDataStoreFactory.Priority.CLOUD)
            cloudExercisesJsonDataStore
        else
            diskExercisesJsonDataStore

}

interface ExercisesDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(priority: Priority): ExercisesJsonDataStore
}