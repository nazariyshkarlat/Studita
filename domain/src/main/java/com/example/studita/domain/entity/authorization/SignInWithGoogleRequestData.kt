package com.example.studita.domain.entity.authorization

import com.example.studita.domain.entity.UserDataData

data class SignInWithGoogleRequestData(val idToken: String, val userDataData: UserDataData?)