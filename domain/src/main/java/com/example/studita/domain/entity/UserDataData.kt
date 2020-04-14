package com.example.studita.domain.entity

import java.util.*

data class UserDataData(var userName: String, var avatarLink: String?, var currentLevel: Int, var currentLevelXP: Int, var streakDays: Int, var isSubscribed: Boolean, val completedParts: ArrayList<Int>, var streakDate: Date)