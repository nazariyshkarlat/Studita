package com.example.studita.domain.service

import com.example.studita.domain.entity.UserIdTokenData

interface SyncNotificationsAreChecked {

    fun scheduleCheckNotifications(userIdTokenData: UserIdTokenData)

}