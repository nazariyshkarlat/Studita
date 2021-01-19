package com.studita.data.net

import com.studita.data.entity.AchievementDataEntity
import com.studita.data.entity.AchievementEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AchievementsService {

    @GET("achievements")
    suspend fun getAchievements(@Query("user_id") user_id: Int): Response<List<AchievementEntity>>

    @GET("achievements_data")
    suspend fun getAchievementsData(@Query("user_id") user_id: Int?): Response<List<AchievementDataEntity>>
}