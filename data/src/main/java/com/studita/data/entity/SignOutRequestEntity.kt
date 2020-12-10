package com.studita.data.entity

import com.studita.domain.entity.SignOutRequestData
import com.google.gson.annotations.SerializedName

data class SignOutRequestEntity(
    @SerializedName("auth_data") val userIdToken: UserIdToken,
    @SerializedName("device_id") val deviceId: String?
)

fun SignOutRequestData.toRawEntity() = SignOutRequestEntity(userIdTokenData.toRawEntity(), deviceId)