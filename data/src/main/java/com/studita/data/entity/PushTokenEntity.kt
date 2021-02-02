package com.studita.data.entity

import com.studita.domain.entity.PushTokenData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PushTokenEntity(
    @SerialName("token") val token: String,
    @SerialName("device_id") val deviceId: String
)

fun PushTokenEntity.toBusinessEntity() = PushTokenData(token, deviceId)
fun PushTokenData.toRawEntity() = PushTokenEntity(token, deviceId)