package com.example.studita.domain.service

import com.example.studita.domain.entity.UserIdTokenData

interface SyncSubscribeEmail {

    fun scheduleSubscribeEmail(subscribe: Boolean, userIdTokenData: UserIdTokenData)

}