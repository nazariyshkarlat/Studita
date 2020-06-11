package com.example.studita.data.entity

import com.example.studita.domain.entity.authorization.SignInWithGoogleRequestData
import com.google.gson.annotations.SerializedName

data class SignInWithGoogleRequest(@SerializedName("id_token")val idToken: String,
                                   @SerializedName("user_data")val userDataEntity: UserDataEntity?,
                                   @SerializedName("user_statistics")val userStatisticsRowEntity: List<UserStatisticsRowEntity>?)

fun SignInWithGoogleRequest.toBusinessEntity() = SignInWithGoogleRequestData(idToken,
    userDataEntity?.toBusinessEntity(),
    userStatisticsRowEntity?.map { it.toBusinessEntity() })
fun SignInWithGoogleRequestData.toRawEntity() = SignInWithGoogleRequest(idToken,
    userDataData?.toRawEntity(),
    userStatistics?.map { it.toRawEntity() })