package com.example.studita.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studita.data.database.completed_exercises.CompletedExercisesDao
import com.example.studita.data.database.user_data.UserDataCompletedPartsTypeConverter
import com.example.studita.data.database.user_data.UserDataDao
import com.example.studita.data.database.user_statistics.UserStatisticsDao
import com.example.studita.data.entity.CompletedExercisesEntity
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserStatisticsRowEntity

@TypeConverters(UserDataCompletedPartsTypeConverter::class)
@Database(
    entities = [UserDataEntity::class, UserStatisticsRowEntity::class, CompletedExercisesEntity::class],
    version = StuditaDatabase.DB_VERSION
)
abstract class StuditaDatabase : RoomDatabase() {

    abstract fun getUserDataDao(): UserDataDao

    abstract fun getCompletedExercisesDao(): CompletedExercisesDao

    abstract fun getUserStatisticsDao(): UserStatisticsDao

    companion object {
        const val DB_VERSION = 3
        const val DB_NAME = "studita_database"
    }
}