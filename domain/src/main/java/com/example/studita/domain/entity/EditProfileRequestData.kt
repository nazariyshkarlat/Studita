package com.example.studita.domain.entity


data class EditProfileData(var userName: String?, var userFullName: String?, var avatarLink: String?)

data class EditProfileRequestData(val userIdTokenData: UserIdTokenData, val editProfileData: EditProfileData)