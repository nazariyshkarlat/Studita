package com.studita.data.net

import com.studita.data.entity.NotificationEntity
import com.studita.data.entity.UserIdToken
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationsService {

    @POST("notifications")
    suspend fun getNotifications(
        @Body userIdToken: UserIdToken,
        @Query("per_page") perPage: Int,
        @Query("page_number") pageNumber: Int
    ): Response<List<NotificationEntity>>

    @POST("notifications_are_checked")
    suspend fun setNotificationsAreChecked(@Body userIdToken: UserIdToken): Response<ResponseBody>
}