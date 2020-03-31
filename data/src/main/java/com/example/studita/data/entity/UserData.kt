package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class UserDataEntity(@SerializedName("user_name")val userName: String, @SerializedName("avatar_link")val avatarLink: String?)