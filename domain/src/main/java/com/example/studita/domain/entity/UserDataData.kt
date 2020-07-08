package com.example.studita.domain.entity

import com.example.studita.domain.interactor.IsMyFriendStatus
import java.util.*

data class UserDataData(val userId: Int?,
                        var userName: String?,
                        var userFullName: String?,
                        var userPublicId: String?,
                        var avatarLink: String?,
                        var currentLevel: Int,
                        var currentLevelXP: Int,
                        var streakDays: Int,
                        var isSubscribed: Boolean,
                        val completedParts: ArrayList<Int>,
                        var streakDatetime: Date,
                        var todayCompletedExercises: Int = 0,
                        var notificationsAreChecked: Boolean)

fun UserDataData.toUserData(isMyFriendStatus: IsMyFriendStatus) = UserData(userId!!, userName!!, avatarLink, isMyFriendStatus)