package com.studita.domain.service

import com.studita.domain.entity.UserIdTokenData

interface SyncNotificationsAreChecked {

    fun scheduleCheckNotifications(userIdTokenData: UserIdTokenData)

}