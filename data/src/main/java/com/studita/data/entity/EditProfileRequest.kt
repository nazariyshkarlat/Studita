package com.studita.data.entity

import com.studita.domain.entity.EditProfileData
import com.studita.domain.entity.EditProfileRequestData
import com.google.gson.annotations.SerializedName

data class EditProfileEntity(
    @SerializedName("user_name") val userName: String? = null,
    @SerializedName("name") val name: String?,
    @SerializedName("avatar_link") val avatarLink: String?,
    @SerializedName("bio") val bio: String?
)

data class EditProfileRequest(
    @SerializedName("auth_data") val userIdToken: UserIdToken,
    @SerializedName("user_data") val editProfileEntity: EditProfileEntity
)

fun EditProfileData.toRawData() = EditProfileEntity(userName, name, avatarLink, bio)
fun EditProfileRequestData.toRawEntity() =
    EditProfileRequest(userIdTokenData.toRawEntity(), editProfileData.toRawData())