package com.studita.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AchievementData(val type: AchievementType, val level: AchievementLevel, val iconUrl: String, val isImprovable: Boolean)

@Serializable
sealed class AchievementDataData{

    abstract val type: AchievementType
    abstract val exercise: String
    abstract val reward: String?
    abstract  val title: String

    @Serializable
    @SerialName("improvable")
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

    @Serializable
    @SerialName("non_improvable")
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

enum class AchievementType(val typeNumber: Int){
    TYPE_STREAK(1),
    TYPE_EXERCISES(2),
    TYPE_TRAININGS(3),
    TYPE_CHAPTERS(4),
    TYPE_FRIEND_ADD(5),
    TYPE_SET_AVATAR(6),
    TYPE_SET_BIO(7),
    TYPE_SET_NAME(8)
}