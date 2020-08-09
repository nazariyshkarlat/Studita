package com.example.studita.domain.entity.authorization

import com.example.studita.domain.entity.PushTokenData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserStatisticsRowData

data class AuthorizationRequestData(
    val userEmail: String,
    val userPassword: String,
    val userDataData: UserDataData?,
    val userStatistics: List<UserStatisticsRowData>?,
    val pushTokenData: PushTokenData?
)
