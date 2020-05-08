package com.example.studita.domain.entity.authorization

import com.example.studita.domain.entity.UserDataData

data class LogInResponseData(val userId: String, val userToken: String, val userDataData: UserDataData)