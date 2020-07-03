package com.example.studita.data.entity

import com.example.studita.domain.entity.authorization.SignInWithGoogleRequestData
import com.google.gson.annotations.SerializedName

data class SignInWithGoogleRequest(@SerializedName("id_token")val idToken: String,
                                   @SerializedName("user_data")val userDataEntity: UserDataEntity?,
                                   @SerializedName("user_statistics")val userStatisticsRowEntity: List<UserStatisticsRowEntity>?,
                                   @SerializedName("push_data")val pushTokenEntity: PushTokenEntity?)

fun SignInWithGoogleRequest.toBusinessEntity() = SignInWithGoogleRequestData(idToken,
    userDataEntity?.toBusinessEntity(),
    userStatisticsRowEntity?.map { it.toBusinessEntity() },
    pushTokenEntity?.toBusinessEntity())

fun SignInWithGoogleRequestData.toRawEntity() = SignInWithGoogleRequest(idToken,
    userDataData?.toRawEntity(),
    userStatistics?.map { it.toRawEntity() },
    pushTokenEntity?.toRawEntity())