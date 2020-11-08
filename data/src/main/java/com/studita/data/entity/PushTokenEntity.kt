package com.studita.data.entity

import com.studita.domain.entity.PushTokenData
import com.google.gson.annotations.SerializedName

data class PushTokenEntity(
    @SerializedName("token") val token: String,
    @SerializedName("device_id") val deviceId: String
)

fun PushTokenEntity.toBusinessEntity() = PushTokenData(token, deviceId)
fun PushTokenData.toRawEntity() = PushTokenEntity(token, deviceId)