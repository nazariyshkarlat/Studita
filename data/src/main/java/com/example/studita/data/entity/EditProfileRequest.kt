package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class EditProfileEntity(@SerializedName("user_name")val userName: String? = null, @SerializedName("user_full_name")val userFullName: String?, @SerializedName("avatar_link")val avatarLink: String?)

data class EditProfileRequest(@SerializedName("auth_data")val userIdToken: UserIdToken, @SerializedName("user_data")val editProfileEntity: EditProfileEntity)