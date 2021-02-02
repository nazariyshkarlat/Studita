package com.studita.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PushTokenData(val token: String, val deviceId: String)