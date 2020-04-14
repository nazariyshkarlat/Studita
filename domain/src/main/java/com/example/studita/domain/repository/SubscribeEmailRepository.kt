package com.example.studita.domain.repository

import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserTokenIdData

interface SubscribeEmailRepository {

    suspend fun subscribe(userTokenIdData: UserTokenIdData): Pair<Int, String>

    suspend fun unsubscribe(userTokenIdData: UserTokenIdData): Pair<Int, String>

}