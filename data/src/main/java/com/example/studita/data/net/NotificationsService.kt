package com.example.studita.data.net

import com.example.studita.data.entity.NotificationEntity
import com.example.studita.data.entity.UserIdToken
import com.example.studita.domain.entity.UserIdTokenData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationsService {

    @POST("notifications")
    suspend fun getNotifications(@Body userIdToken: UserIdToken, @Query("per_page")perPage: Int, @Query("page_number")pageNumber: Int) : Response<List<NotificationEntity>>

}