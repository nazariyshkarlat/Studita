package com.studita.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class EditDuelsExceptionsRequestData(
    val userIdTokenData: UserIdTokenData,
    val editDuelsExceptionsData: List<EditDuelsExceptionsData>
)

@Serializable
data class EditDuelsExceptionsData(
    val deleteFromExceptions: Boolean,
    val exceptionId: Int,
    val userName: String
)
