package com.studita.data.entity

import com.studita.domain.entity.authorization.SignInWithGoogleRequestData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInWithGoogleRequest(
    @SerialName("id_token") val idToken: String,
    @SerialName("user_data") val userDataEntity: UserDataEntity?,
    @SerialName("user_statistics") val userStatisticsRowEntity: List<UserStatisticsRowEntity>?,
    @SerialName("push_data") val pushTokenEntity: PushTokenEntity?
)

fun SignInWithGoogleRequest.toBusinessEntity() = SignInWithGoogleRequestData(
    idToken,
    userDataEntity?.toBusinessEntity(),
    userStatisticsRowEntity?.map { it.toBusinessEntity() },
    pushTokenEntity?.toBusinessEntity()
)

fun SignInWithGoogleRequestData.toRawEntity() = SignInWithGoogleRequest(
    idToken,
    userDataData?.toRawEntity(),
    userStatistics?.map { it.toRawEntity() },
    pushTokenEntity?.toRawEntity()
)