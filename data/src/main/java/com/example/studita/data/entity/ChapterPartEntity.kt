package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class ChapterPartEntity(
    @SerializedName("number") val partNumber: Int,
    @SerializedName("name") val partName: String
)