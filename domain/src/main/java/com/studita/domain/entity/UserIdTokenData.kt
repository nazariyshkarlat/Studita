package com.studita.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserIdTokenData(val userId: Int, val userToken: String)