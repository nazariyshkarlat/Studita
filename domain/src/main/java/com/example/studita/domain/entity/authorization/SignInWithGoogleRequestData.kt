package com.example.studita.domain.entity.authorization

import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserStatisticsRowData

data class SignInWithGoogleRequestData(val idToken: String, val userDataData: UserDataData?, val userStatistics: List<UserStatisticsRowData>?)