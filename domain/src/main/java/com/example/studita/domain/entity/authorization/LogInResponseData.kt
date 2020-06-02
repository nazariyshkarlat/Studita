package com.example.studita.domain.entity.authorization

import com.example.studita.domain.entity.UserDataData

data class LogInResponseData(val userId: Int, val userToken: String, val userDataData: UserDataData)