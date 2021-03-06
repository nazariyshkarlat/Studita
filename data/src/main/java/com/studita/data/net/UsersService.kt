package com.studita.data.net

import com.studita.data.entity.FriendActionRequest
import com.studita.data.entity.IsMyFriendEntity
import com.studita.data.entity.UsersResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UsersService {

    @GET("get_users")
    suspend fun getUsers(
        @Query("user_id") userId: Int,
        @Query("friend_of_user_id") friendOfUserId: Int?,
        @Query("per_page") perPage: Int,
        @Query("page_number") page_number: Int,
        @Query("sort_by") sortBy: String?,
        @Query("starts_with") startsWith: String?
    ): Response<UsersResponse>

    @GET("is_my_friend")
    suspend fun checkIsMyFriend(
        @Query("my_id") myId: Int,
        @Query("user_id") userId: Int
    ): Response<IsMyFriendEntity>

    @POST("send_friendship")
    suspend fun sendFriendship(@Body friendActionRequest: FriendActionRequest): Response<ResponseBody>

    @POST("remove_friend")
    suspend fun removeFriend(@Body friendActionRequest: FriendActionRequest): Response<ResponseBody>

    @POST("cancel_friendship")
    suspend fun cancelFriendship(@Body friendActionRequest: FriendActionRequest): Response<ResponseBody>

    @POST("accept_friendship")
    suspend fun acceptFriendship(@Body friendActionRequest: FriendActionRequest): Response<ResponseBody>

    @POST("reject_friendship")
    suspend fun rejectFriendship(@Body friendActionRequest: FriendActionRequest): Response<ResponseBody>

    @GET("has_friends")
    suspend fun hasFriends(@Query("user_id") userId: Int): Response<Boolean?>
}