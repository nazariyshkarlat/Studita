package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class SubscribeEmailResultEntity(@SerializedName("subscribe")val subscribe: Boolean, @SerializedName("email")val email: String?)