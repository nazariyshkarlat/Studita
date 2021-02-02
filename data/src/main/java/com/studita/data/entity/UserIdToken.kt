package com.studita.data.entity

import com.studita.domain.entity.UserIdTokenData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserIdToken(
    @SerialName("user_id") val userId: Int,
    @SerialName("user_token") val userToken: String
)

fun UserIdTokenData.toRawEntity() = UserIdToken(userId, userToken)
fun UserIdToken.toBusinessEntity() = UserIdTokenData(userId, userToken)