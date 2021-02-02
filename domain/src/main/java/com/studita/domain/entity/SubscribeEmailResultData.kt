package com.studita.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class SubscribeEmailResultData(val subscribe: Boolean, val email: String?)