package com.studita.domain.entity

import com.google.gson.annotations.SerializedName

data class AchievementData(val type: AchievementType, val level: AchievementLevel, val iconUrl: String, val isImprovable: Boolean)

sealed class AchievementDataData{

    abstract val type: AchievementType
    abstract val exercise: String
    abstract val reward: String?
    abstract  val title: String

    data class ImprovableAchievementData(
        override val type: AchievementType,
        override val exercise: String,
        override val reward: String?,
        override val title: String,
        val currentLevel: AchievementLevel,
        val currentLevelIcon: String?,
        val nextLevelIcon: String?,
        val maxLevel: AchievementLevel,
        val progressType: String,
        val currentProgress: Int,
        val maxProgress: Int
    ) : AchievementDataData()

    data class NonImprovableAchievementData(
        override val type: AchievementType,
        override val exercise: String,
        override val reward: String,
        override val title: String,
        val iconUrl: String,
        val isCompleted: Boolean
    ) : AchievementDataData()
}

enum class AchievementLevel(val levelNumber: Int) {
    LEVEL_BRONZE(1),
    LEVEL_SILVER(2),
    LEVEL_GOLD(3),
    LEVEL_DIAMOND(4),
    NO_LEVEL(0)
}

enum class AchievementType{
    TYPE_STREAK,
    TYPE_EXERCISES,
    TYPE_TRAININGS,
    TYPE_CHAPTERS,
    TYPE_LEVELS,
    TYPE_FRIEND_ADD,
    TYPE_SET_AVATAR,
    TYPE_SET_BIO,
    TYPE_SET_NAME
}