package com.studita.domain.service

import com.studita.domain.entity.SignOutRequestData

interface SyncSignOut {

    fun scheduleSignOut(signOutRequestData: SignOutRequestData)

}