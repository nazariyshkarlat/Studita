package com.studita.domain.entity


data class EditProfileData(var userName: String?, var name: String?, var bio: String?, var avatarLink: String?)

data class EditProfileRequestData(
    val userIdTokenData: UserIdTokenData,
    val editProfileData: EditProfileData
)