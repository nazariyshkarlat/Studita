package com.example.studita.data.entity

import com.example.studita.domain.entity.PrivacyDuelsExceptionData
import com.google.gson.annotations.SerializedName

data class PrivacyDuelsExceptionsEntity(@SerializedName("user_id")val userId: Int,
                                                                                                                                        @SerializedName("user_name")val userName: String,
                                                                                                                                        @SerializedName("avatar_link")val avatarLink: String?,
                                                                                                                                        @SerializedName("is_exception")val isException: Boolean)

fun PrivacyDuelsExceptionsEntity.toBusinessEntity() = PrivacyDuelsExceptionData(userId, userName, avatarLink, isException)