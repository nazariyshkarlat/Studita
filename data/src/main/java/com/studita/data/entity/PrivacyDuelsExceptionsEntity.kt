package com.studita.data.entity

import com.studita.domain.entity.PrivacyDuelsExceptionData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrivacyDuelsExceptionsEntity(
    @SerialName("user_id") val userId: Int,
    @SerialName("user_name") val userName: String,
    @SerialName("avatar_link") val avatarLink: String?,
    @SerialName("is_exception") val isException: Boolean
)

fun PrivacyDuelsExceptionsEntity.toBusinessEntity() =
    PrivacyDuelsExceptionData(userId, userName, avatarLink, isException)