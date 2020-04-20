package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class UserTokenId(@SerializedName("user_id")val userId: String, @SerializedName("user_token")val userToken: String)