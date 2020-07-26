package com.example.studita.domain.repository

import com.example.studita.domain.entity.NotificationData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.entity.UsersResponseData

interface NotificationsRepository {

    suspend fun getNotifications(userIdTokenData: UserIdTokenData, perPage: Int, pageNumber: Int): Pair<Int, List<NotificationData>?>

    suspend fun setNotificationsAreChecked(userIdTokenData: UserIdTokenData): Int
}