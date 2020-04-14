package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.UserTokenIdMapper
import com.example.studita.data.repository.subscribe_mail.SubscribeEmailDataStoreFactory
import com.example.studita.domain.entity.UserTokenIdData
import com.example.studita.domain.repository.SubscribeEmailRepository

class SubscribeEmailRepositoryImpl(private val subscribeEmailDataStoreFactory: SubscribeEmailDataStoreFactory) : SubscribeEmailRepository{
    override suspend fun subscribe(userTokenIdData: UserTokenIdData): Pair<Int, String> = subscribeEmailDataStoreFactory.create(SubscribeEmailDataStoreFactory.Priority.CLOUD).trySubscribe(UserTokenIdMapper().map(userTokenIdData))

    override suspend fun unsubscribe(userTokenIdData: UserTokenIdData): Pair<Int, String> = subscribeEmailDataStoreFactory.create(SubscribeEmailDataStoreFactory.Priority.CLOUD).tryUnsubscribe(UserTokenIdMapper().map(userTokenIdData))

}