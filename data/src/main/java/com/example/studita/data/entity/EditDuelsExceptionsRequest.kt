package com.example.studita.data.entity

import com.example.studita.domain.entity.EditDuelsExceptionsData
import com.example.studita.domain.entity.EditDuelsExceptionsRequestData
import com.google.gson.annotations.SerializedName

data class EditDuelsExceptionsRequest(@SerializedName("auth_data")val userIdToken: UserIdToken,
                                      @SerializedName("exceptions_data")val editDuelsExceptionsEntities: List<EditDuelsExceptionsEntity>)

data class EditDuelsExceptionsEntity(@SerializedName("delete")val deleteFromExceptions: Boolean, @SerializedName("exception_id")val exceptionId: Int)


fun EditDuelsExceptionsRequestData.toRawEntity() =
    EditDuelsExceptionsRequest(
        userIdTokenData.toRawEntity(),
        editDuelsExceptionsData.map{it.toRawEntity()}
    )

fun EditDuelsExceptionsData.toRawEntity() = EditDuelsExceptionsEntity(deleteFromExceptions, exceptionId)