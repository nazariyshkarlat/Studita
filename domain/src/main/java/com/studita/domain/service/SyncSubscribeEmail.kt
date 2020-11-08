package com.studita.domain.service

import com.studita.domain.entity.UserIdTokenData

interface SyncSubscribeEmail {

    fun scheduleSubscribeEmail(subscribe: Boolean, userIdTokenData: UserIdTokenData)

}