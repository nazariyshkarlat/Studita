package com.example.studita.data.database.user_data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.studita.data.entity.UserDataEntity
import kotlinx.coroutines.Deferred

@Dao
interface UserDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserData(userDataEntity: UserDataEntity)

    @Query("DELETE FROM ${UserDataEntity.TABLE_NAME}")
    suspend fun deleteUserData()

    @Query("SELECT * FROM ${UserDataEntity.TABLE_NAME}")
    suspend fun getUserDataAsync(): UserDataEntity

}