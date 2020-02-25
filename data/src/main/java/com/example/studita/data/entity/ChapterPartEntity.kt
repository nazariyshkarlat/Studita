package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class ChapterPartEntity(
    @SerializedName("part_number") val partNumber: Int,
    @SerializedName("part_name") val partName: String
)