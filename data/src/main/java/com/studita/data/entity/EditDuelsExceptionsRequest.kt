package com.studita.data.entity

import com.studita.domain.entity.EditDuelsExceptionsData
import com.studita.domain.entity.EditDuelsExceptionsRequestData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditDuelsExceptionsRequest(
    @SerialName("auth_data") val userIdToken: UserIdToken,
    @SerialName("exceptions_data") val editDuelsExceptionsEntities: List<EditDuelsExceptionsEntity>
)

@Serializable
data class EditDuelsExceptionsEntity(
    @SerialName("delete") val deleteFromExceptions: Boolean,
    @SerialName("exception_id") val exceptionId: Int
)


fun EditDuelsExceptionsRequestData.toRawEntity() =
    EditDuelsExceptionsRequest(
        userIdTokenData.toRawEntity(),
        editDuelsExceptionsData.map { it.toRawEntity() }
    )

fun EditDuelsExceptionsData.toRawEntity() =
    EditDuelsExceptionsEntity(deleteFromExceptions, exceptionId)