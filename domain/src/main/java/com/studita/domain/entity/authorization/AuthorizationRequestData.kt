package com.studita.domain.entity.authorization

import com.studita.domain.entity.PushTokenData
import com.studita.domain.entity.UserDataData
import com.studita.domain.entity.UserStatisticsRowData

data class AuthorizationRequestData(
    val userEmail: String,
    val userPassword: String,
    val userDataData: UserDataData?,
    val userStatistics: List<UserStatisticsRowData>?,
    val pushTokenData: PushTokenData?,
    val isFirstLogIn: Boolean = false
)
