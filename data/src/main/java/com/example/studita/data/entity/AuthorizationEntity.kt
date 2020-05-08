package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class LogInResponseEntity(
    @SerializedName("user_id") val userId: String,
    @SerializedName("user_token") val userToken: String,
    @SerializedName("user_data")val userDataEntity: UserDataEntity
)

data class AuthorizationRequestEntity(@SerializedName("user_email")val userEmail: String,
                                      @SerializedName("user_password")val userPassword: String,
                                      @SerializedName("user_data")val userDataEntity: UserDataEntity?,
                                      @SerializedName("user_statistics")val userStatisticsRowEntity: List<UserStatisticsRowEntity>?)
