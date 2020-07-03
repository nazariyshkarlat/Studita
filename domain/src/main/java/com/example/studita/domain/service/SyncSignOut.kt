package com.example.studita.domain.service

import com.example.studita.domain.entity.SignOutRequestData
import com.example.studita.domain.entity.UserIdTokenData

interface SyncSignOut {

    fun scheduleSignOut(signOutRequestData: SignOutRequestData)

}