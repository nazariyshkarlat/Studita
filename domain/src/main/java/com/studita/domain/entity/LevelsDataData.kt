package com.studita.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class LevelsDataData(
    var chapterOneCompletedCount: Int,
    var chapterTwoCompletedCount: Int,
    var chapterThreeCompletedCount: Int,
    var chapterFourCompletedCount: Int
)