package com.studita.data.entity

import com.studita.domain.entity.SignOutRequestData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignOutRequestEntity(
    @SerialName("auth_data") val userIdToken: UserIdToken,
    @SerialName("device_id") val deviceId: String?
)

fun SignOutRequestData.toRawEntity() = SignOutRequestEntity(userIdTokenData.toRawEntity(), deviceId)