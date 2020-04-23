package com.example.studita.domain.entity.authorization

import com.example.studita.domain.entity.UserDataData

data class AuthorizationRequestData(val userEmail: String, val userPassword: String, val userDataData: UserDataData?)