package com.studita.data.entity

import com.studita.domain.entity.EditProfileData
import com.studita.domain.entity.EditProfileRequestData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditProfileEntity(
    @SerialName("user_name") val userName: String? = null,
    @SerialName("name") val name: String?,
    @SerialName("avatar_link") val avatarLink: String?,
    @SerialName("bio") val bio: String?
)

@Serializable
data class EditProfileRequest(
    @SerialName("auth_data") val userIdToken: UserIdToken,
    @SerialName("user_data") val editProfileEntity: EditProfileEntity
)

fun EditProfileData.toRawData() = EditProfileEntity(userName, name, avatarLink, bio)
fun EditProfileRequestData.toRawEntity() =
    EditProfileRequest(userIdTokenData.toRawEntity(), editProfileData.toRawData())