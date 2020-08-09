package com.example.studita.domain.service

import com.example.studita.domain.entity.SignOutRequestData

interface SyncSignOut {

    fun scheduleSignOut(signOutRequestData: SignOutRequestData)

}