package com.example.studita.data.database.completed_exercises

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.studita.data.entity.CompletedExercisesEntity
import com.example.studita.data.entity.UserDataEntity

@Dao
interface CompletedExercisesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedExercisesEntity(completedExercisesEntity: CompletedExercisesEntity)

    @Query("DELETE FROM ${CompletedExercisesEntity.TABLE_NAME} WHERE datetime = :datetimeCompleted")
    suspend fun deleteCompletedExercises(datetimeCompleted: String)

    @Query("DELETE FROM ${CompletedExercisesEntity.TABLE_NAME}")
    suspend fun clearCompletedExercises()

    @Query("SELECT * FROM ${CompletedExercisesEntity.TABLE_NAME}")
    suspend fun getCompletedExercisesEntity(): List<CompletedExercisesEntity>?

}