package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class ChapterEntity(@SerializedName("title")val title : String, @SerializedName("parts")val parts: List<ChapterPartEntity>)

data class ChapterPartEntity(
    @SerializedName("number") val number: Int,
    @SerializedName("name") val name: String
)