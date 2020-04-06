package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class UserDataEntity(@SerializedName("user_name")val userName: String, @SerializedName("avatar_link")val avatarLink: String?, @SerializedName("current_level")val currentLevel: Int, @SerializedName("current_level_XP")val currentLevelXP: Int,  @SerializedName("streak_days")val streakDays: Int)