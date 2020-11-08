package com.studita.data.entity

import com.studita.domain.entity.UserIdTokenData
import com.google.gson.annotations.SerializedName

data class UserIdToken(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_token") val userToken: String
)

fun UserIdTokenData.toRawEntity() = UserIdToken(userId, userToken)
fun UserIdToken.toBusinessEntity() = UserIdTokenData(userId, userToken)