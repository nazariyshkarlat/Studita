package com.studita.domain.entity

data class EditDuelsExceptionsRequestData(
    val userIdTokenData: UserIdTokenData,
    val editDuelsExceptionsData: List<EditDuelsExceptionsData>
)

data class EditDuelsExceptionsData(
    val deleteFromExceptions: Boolean,
    val exceptionId: Int,
    val userName: String
)
