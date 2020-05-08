package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class SignInWithGoogleRequestEntity(@SerializedName("id_token")val idToken: String,
                                         @SerializedName("user_data")val userDataEntity: UserDataEntity?,
                                         @SerializedName("user_statistics")val userStatisticsRowEntity: List<UserStatisticsRowEntity>?)