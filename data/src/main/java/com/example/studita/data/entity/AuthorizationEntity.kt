package com.example.studita.data.entity

import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.authorization.LogInResponseData
import com.google.gson.annotations.SerializedName

data class LogInResponseEntity(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_token") val userToken: String,
    @SerializedName("user_data")val userDataEntity: UserDataEntity
)

data class AuthorizationRequestEntity(@SerializedName("user_email")val userEmail: String,
                                      @SerializedName("user_password")val userPassword: String,
                                      @SerializedName("user_data")val userDataEntity: UserDataEntity?,
                                      @SerializedName("user_statistics")val userStatisticsRowEntity: List<UserStatisticsRowEntity>?,
                                      @SerializedName("push_data")val pushTokenEntity: PushTokenEntity?)

fun AuthorizationRequestData.toRawEntity() = AuthorizationRequestEntity(userEmail,
    userPassword,
    userDataData?.toRawEntity() ,
    userStatistics?.map { it.toRawEntity()},
    pushTokenData?.toRawEntity())

fun LogInResponseEntity.toBusinessEntity() = LogInResponseData(
    userId,
    userToken,
    userDataEntity.toBusinessEntity()
)

