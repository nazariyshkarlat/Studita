package com.example.studita.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.studita.data.entity.UserDataEntity.Companion.TABLE_NAME
import com.google.gson.annotations.SerializedName

@Entity(tableName = TABLE_NAME)
data class UserDataEntity(@SerializedName("user_name")val userName: String?,
                          @SerializedName("user_public_id")val userPublicId: String?,
                          @SerializedName("avatar_link")val avatarLink: String?,
                          @SerializedName("current_level")val currentLevel: Int,
                          @SerializedName("current_level_XP")val currentLevelXP: Int,
                          @SerializedName("streak_days")val streakDays: Int,
                          @SerializedName("subscribed")val isSubscribed: Boolean,
                          @SerializedName("completed_parts")val completedParts: List<Int>,
                          @SerializedName("streak_datetime")val streakDatetime: String){
    companion object {
        const val TABLE_NAME = "user_data"
    }
    @PrimaryKey
    @SerializedName("user_local_id")
    var userLocalId: Int = 1
}
