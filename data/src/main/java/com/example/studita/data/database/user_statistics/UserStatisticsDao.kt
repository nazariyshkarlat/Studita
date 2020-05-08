package com.example.studita.data.database.user_statistics

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserStatisticsRowEntity

@Dao
interface UserStatisticsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStatistics(userStatisticsRowEntity: UserStatisticsRowEntity)

    @Query("DELETE FROM ${UserStatisticsRowEntity.TABLE_NAME}")
    suspend fun deleteUserStatistics()

    @Query("SELECT * FROM ${UserStatisticsRowEntity.TABLE_NAME}")
    suspend fun getUserStatistics(): List<UserStatisticsRowEntity>?

}