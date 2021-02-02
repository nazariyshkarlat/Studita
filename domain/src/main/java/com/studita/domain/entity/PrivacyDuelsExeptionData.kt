package com.studita.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PrivacyDuelsExceptionData(
    val userId: Int,
    val userName: String,
    val avatarLink: String?,
    val isException: Boolean
)