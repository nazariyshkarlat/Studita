package com.studita.data.entity
import com.studita.domain.entity.AchievementData
import com.studita.domain.entity.AchievementDataData
import com.studita.domain.entity.AchievementLevel
import com.studita.domain.entity.AchievementType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.lang.UnsupportedOperationException

@Serializable
data class AchievementEntity(@SerialName("type") val type: Int,
                             @SerialName("level") val level: Int,
                             @SerialName("icon_url") val iconUrl: String,
                             @SerialName("improvable") val improvable: Boolean)

@Serializable
data class AchievementDataEntity(@SerialName("type") val type: Int,
                                 @SerialName("improvable") val improvable: Boolean,
                                 @SerialName("exercise") val exercise: String,
                                 @SerialName("reward") val reward: String? = null,
                                 @SerialName("title") val title: String,
                                 @SerialName("current_level") val currentLevel: Int? = null,
                                 @SerialName("current_level_icon") val currentLevelIcon: String? = null,
                                 @SerialName("next_level_icon") val nextLevelIcon: String? = null,
                                 @SerialName("icon_url") val iconUrl: String? = null,
                                 @SerialName("progress_type") val progressType: String? = null,
                                 @SerialName("max_level") val maxLevel: Int? = null,
                                 @SerialName("completed") val completed: Boolean? = null,
                                 @SerialName("current_progress") val currentProgress: Int? = null,
                                 @SerialName("max_progress") val maxProgress: Int? = null)

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
    5 -> AchievementType.TYPE_FRIEND_ADD
    6 -> AchievementType.TYPE_SET_AVATAR
    7 -> AchievementType.TYPE_SET_BIO
    8 -> AchievementType.TYPE_SET_NAME
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