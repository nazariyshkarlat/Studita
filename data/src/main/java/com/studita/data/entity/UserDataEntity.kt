package com.studita.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studita.data.entity.UserDataEntity.Companion.TABLE_NAME
import com.studita.domain.date.DateTimeFormat
import com.studita.domain.entity.UserDataData
import com.google.gson.annotations.SerializedName

@Entity(tableName = TABLE_NAME)
data class UserDataEntity(
    @ColumnInfo(name = "user_id")
    @SerializedName("user_id") val userId: Int? = null,
    @ColumnInfo(name = "user_name")
    @SerializedName("user_name") val userName: String? = null,
    @ColumnInfo(name = "name")
    @SerializedName("name") val name: String? = null,
    @ColumnInfo(name = "bio")
    @SerializedName("bio") val bio: String? = null,
    @ColumnInfo(name = "user_public_id")
    @SerializedName("user_public_id") val userPublicId: String? = null,
    @ColumnInfo(name = "avatar_link")
    @SerializedName("avatar_link") val avatarLink: String? = null,
    @ColumnInfo(name = "current_level")
    @SerializedName("current_level") val currentLevel: Int = 0,
    @ColumnInfo(name = "current_level_XP")
    @SerializedName("current_level_XP") val currentLevelXP: Int = 0,
    @ColumnInfo(name = "streak_days")
    @SerializedName("streak_days") val streakDays: Int = 0,
    @ColumnInfo(name = "subscribed")
    @SerializedName("subscribed") val isSubscribed: Boolean = false,
    @ColumnInfo(name = "completed_parts")
    @SerializedName("completed_parts") val completedParts: List<Int> = listOf(),
    @ColumnInfo(name = "streak_datetime")
    @SerializedName("streak_datetime") val streakDatetime: String = "1900-01-01 00:00:00",
    @ColumnInfo(name = "today_completed_exercises")
    @SerializedName("today_completed_exercises") val todayCompletedExercises: Int = 0,
    @ColumnInfo(name = "notifications_are_checked")
    @SerializedName("notifications_are_checked") val notificationsAreChecked: Boolean = true
) {
    companion object {
        const val TABLE_NAME = "user_data"
    }

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "user_local_id")
    @Transient
    var userLocalId: Long = 1
}

fun UserDataData.toRawEntity() = UserDataEntity(
    userId,
    userName,
    name,
    bio,
    userPublicId,
    avatarLink,
    currentLevel,
    currentLevelXP,
    streakDays,
    isSubscribed,
    ArrayList(completedParts),
    DateTimeFormat().format(streakDatetime),
    todayCompletedExercises
)

fun UserDataEntity.toBusinessEntity() = UserDataData(
    userId,
    userName,
    name,
    bio,
    userPublicId,
    avatarLink,
    currentLevel,
    currentLevelXP,
    streakDays,
    isSubscribed,
    ArrayList(completedParts),
    DateTimeFormat().parse(streakDatetime)!!,
    todayCompletedExercises,
    notificationsAreChecked
)
