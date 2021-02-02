package com.studita.domain.entity.authorization

import com.studita.domain.entity.UserDataData
import kotlinx.serialization.Serializable

@Serializable
data class LogInResponseData(val userId: Int, val userToken: String, val userDataData: UserDataData, val isAfterSignUp: Boolean)