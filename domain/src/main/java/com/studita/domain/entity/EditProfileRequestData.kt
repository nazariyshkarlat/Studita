package com.studita.domain.entity

import kotlinx.serialization.Serializable


@Serializable
data class EditProfileData(var userName: String?, var name: String?, var bio: String?, var avatarLink: String?)

@Serializable
data class EditProfileRequestData(
    val userIdTokenData: UserIdTokenData,
    val editProfileData: EditProfileData
)