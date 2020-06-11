package com.example.studita.data.entity

import com.example.studita.domain.entity.SubscribeEmailResultData
import com.google.gson.annotations.SerializedName

data class SubscribeEmailResultEntity(@SerializedName("subscribe")val subscribe: Boolean, @SerializedName("email")val email: String?)

fun SubscribeEmailResultEntity.toBusinessEntity() = SubscribeEmailResultData(subscribe, email)
fun SubscribeEmailResultData.toRawEntity() = SubscribeEmailResultEntity(subscribe, email)