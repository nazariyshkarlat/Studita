package com.example.studita.data.net

import com.example.studita.data.entity.FriendActionRequest
import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.entity.FriendEntity
import com.example.studita.data.entity.FriendsResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendsService {
    
    @GET("get_friends")
    suspend fun getFriends(@Query("user_id") userId: Int, @Query("start_index") startIndex: Int, @Query("end_index") endIndex: Int): Response<FriendsResponse>


    @GET("is_my_friend")
    suspend fun checkIsMyFriend(@Query("user_id") userId: Int, @Query("another_user_id") startIndex: Int): Response<Boolean>

    @POST("add_friend")
    suspend fun addFriend(@Body friendActionRequest: FriendActionRequest): Response<ResponseBody>

    @POST("remove_friend")
    suspend fun removeFriend(@Body friendActionRequest: FriendActionRequest): Response<ResponseBody>
}