package com.studita.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class SignOutRequestData(val userIdTokenData: UserIdTokenData, val deviceId: String?)