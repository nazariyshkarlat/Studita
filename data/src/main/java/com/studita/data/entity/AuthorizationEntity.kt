package com.studita.data.entity

import com.studita.domain.entity.authorization.AuthorizationRequestData
import com.studita.domain.entity.authorization.LogInResponseData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogInResponseEntity(
    @SerialName("user_id") val userId: Int,
    @SerialName("user_token") val userToken: String,
    @SerialName("user_data") val userDataEntity: UserDataEntity,
    @SerialName("is_after_sign_up") val isAfterSignUp: Boolean? = null
)

@Serializable
data class AuthorizationRequestEntity(
    @SerialName("user_email") val userEmail: String,
    @SerialName("user_password") val userPassword: String,
    @SerialName("user_data") val userDataEntity: UserDataEntity?,
    @SerialName("user_statistics") val userStatisticsRowEntity: List<UserStatisticsRowEntity>?,
    @SerialName("push_data") val pushTokenEntity: PushTokenEntity?,
    @SerialName("is_first_log_in") val isFirstLogIn: Boolean = false
)

fun AuthorizationRequestData.toRawEntity() = AuthorizationRequestEntity(
    userEmail,
    userPassword,
    userDataData?.toRawEntity(),
    userStatistics?.map { it.toRawEntity() },
    pushTokenData?.toRawEntity(),
    isFirstLogIn
)

fun LogInResponseEntity.toBusinessEntity() = LogInResponseData(
    userId,
    userToken,
    userDataEntity.toBusinessEntity(),
    isAfterSignUp == true
)

