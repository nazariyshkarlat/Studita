package com.example.studita.domain.interactor.subscribe_email

import android.content.SharedPreferences
import com.example.studita.domain.entity.SubscribeEmailResultData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.*

interface SubscribeEmailInteractor{

    suspend fun subscribe(userIdTokenData: UserIdTokenData): SubscribeEmailResultStatus
    suspend fun unsubscribe(userIdTokenData: UserIdTokenData): SubscribeEmailResultStatus
    suspend fun saveSyncedResult(status: SubscribeEmailResultStatus)
    fun getSyncedResult(): SubscribeEmailResultData?
}