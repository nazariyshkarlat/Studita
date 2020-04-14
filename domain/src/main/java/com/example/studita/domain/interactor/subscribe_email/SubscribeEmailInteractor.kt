package com.example.studita.domain.interactor.subscribe_email

import com.example.studita.domain.entity.UserTokenIdData
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.interactor.*

interface SubscribeEmailInteractor{

    suspend fun subscribe(userTokenIdData: UserTokenIdData): SubscribeEmailStatus
    suspend fun unsubscribe(userTokenIdData: UserTokenIdData): SubscribeEmailStatus

}