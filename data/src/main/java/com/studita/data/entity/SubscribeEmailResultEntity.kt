package com.studita.data.entity

import com.studita.domain.entity.SubscribeEmailResultData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscribeEmailResultEntity(
    @SerialName("subscribe") val subscribe: Boolean,
    @SerialName("email") val email: String?
)

fun SubscribeEmailResultEntity.toBusinessEntity() = SubscribeEmailResultData(subscribe, email)
fun SubscribeEmailResultData.toRawEntity() = SubscribeEmailResultEntity(subscribe, email)