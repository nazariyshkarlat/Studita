package com.studita.domain.entity.authorization

import com.studita.domain.entity.PushTokenData
import com.studita.domain.entity.UserDataData
import com.studita.domain.entity.UserStatisticsRowData

data class SignInWithGoogleRequestData(
    val idToken: String,
    val userDataData: UserDataData?,
    val userStatistics: List<UserStatisticsRowData>?,
    val pushTokenEntity: PushTokenData?
)