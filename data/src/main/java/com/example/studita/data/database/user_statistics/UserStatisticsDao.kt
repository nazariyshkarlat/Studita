package com.example.studita.data.database.user_statistics

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserStatisticsEntity

@Dao
interface UserStatisticsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStatistics(userStatisticsEntity: UserStatisticsEntity)

    @Query("DELETE FROM ${UserStatisticsEntity.TABLE_NAME} WHERE timeType = :timeType")
    suspend fun deleteUserStatistics(timeType: String)

    @Query("SELECT * FROM ${UserStatisticsEntity.TABLE_NAME} WHERE timeType = :timeType")
    suspend fun getUserStatistics(timeType: String): UserStatisticsEntity

}