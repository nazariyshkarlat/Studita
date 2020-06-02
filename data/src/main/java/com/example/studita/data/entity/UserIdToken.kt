package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class UserIdToken(@SerializedName("user_id")val userId: Int, @SerializedName("user_token")val userToken: String)