package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class LogInResponseEntity(
    @SerializedName("token_id") val userTokenId: String,
    @SerializedName("user_token") val userToken: String
)

data class AuthorizationRequestEntity(@SerializedName("user_email")val userEmail: String, @SerializedName("user_password")val userPassword: String)
