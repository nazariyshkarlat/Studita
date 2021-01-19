package com.studita.data.entity

import com.google.gson.annotations.SerializedName
import com.studita.domain.entity.AchievementData
import com.studita.domain.entity.AchievementDataData
import com.studita.domain.entity.AchievementLevel
import com.studita.domain.entity.AchievementType
import java.lang.UnsupportedOperationException

data class AchievementEntity(@SerializedName("type") val type: Int,
                             @SerializedName("level") val level: Int,
                             @SerializedName("icon_url") val iconUrl: String,
                             @SerializedName("improvable") val improvable: Boolean)

data class AchievementDataEntity(@SerializedName("type") val type: Int,
                                 @SerializedName("improvable") val improvable: Boolean,
                                 @SerializedName("exercise") val exercise: String,
                                 @SerializedName("reward") val reward: String?,
                                  @SerializedName("title") val title: String,
                                  @SerializedName("current_level") val currentLevel: Int?,
                                 @SerializedName("current_level_icon") val currentLevelIcon: String?,
                                 @SerializedName("next_level_icon") val nextLevelIcon: String?,
                                 @SerializedName("icon_url") val iconUrl: String?,
                                 @SerializedName("progress_type") val progressType: String?,
                                 @SerializedName("max_level") val maxLevel: Int?,
                                 @SerializedName("completed") val completed: Boolean?,
                                 @SerializedName("current_progress") val currentProgress: Int?,
                                 @SerializedName("max_progress") val maxProgress: Int?)

fun AchievementDataEntity.toBusinessEntity() =
    if(improvable)
        AchievementDataData.ImprovableAchievementData(
            type.toAchievementType(),
            exercise,
            reward,
            title,
            currentLevel.toAchievementLevel(),
            currentLevelIcon,
            nextLevelIcon,
            maxLevel.toAchievementLevel(),
            progressType!!,
            currentProgress!!,
            maxProgress!!
        )
    else
        AchievementDataData.NonImprovableAchievementData(
            type.toAchievementType(),
            exercise,
            reward!!,
            title,
            iconUrl!!,
            completed!!
        )



fun AchievementEntity.toBusinessEntity() = AchievementData(type.toAchievementType(), level.toAchievementLevel(), iconUrl, improvable)

fun Int.toAchievementType() = when(this){
    1 -> AchievementType.TYPE_STREAK
    2 -> AchievementType.TYPE_EXERCISES
    3 -> AchievementType.TYPE_TRAININGS
    4 -> AchievementType.TYPE_CHAPTERS
    5 -> AchievementType.TYPE_LEVELS
    6 -> AchievementType.TYPE_FRIEND_ADD
    7 -> AchievementType.TYPE_SET_AVATAR
    8 -> AchievementType.TYPE_SET_BIO
    9 -> AchievementType.TYPE_SET_NAME
    else -> throw UnsupportedOperationException("unknown achievement type")
}

fun Int?.toAchievementLevel() = when(this){
    1 -> AchievementLevel.LEVEL_BRONZE
    2 -> AchievementLevel.LEVEL_SILVER
    3 -> AchievementLevel.LEVEL_GOLD
    4 -> AchievementLevel.LEVEL_DIAMOND
    0 -> AchievementLevel.NO_LEVEL
    null -> AchievementLevel.NO_LEVEL
    else -> throw UnsupportedOperationException("unknown achievement level")
}