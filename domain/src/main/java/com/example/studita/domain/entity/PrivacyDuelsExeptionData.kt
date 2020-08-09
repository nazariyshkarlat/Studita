package com.example.studita.domain.entity

data class PrivacyDuelsExceptionData(
    val userId: Int,
    val userName: String,
    val avatarLink: String?,
    val isException: Boolean
)