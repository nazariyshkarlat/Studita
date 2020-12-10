package com.studita.data.repository

import com.studita.data.cache.subscribe_email.SubscribeEmailCache
import com.studita.data.entity.SubscribeEmailResultEntity
import com.studita.data.entity.toBusinessEntity
import com.studita.data.entity.toRawEntity
import com.studita.data.repository.datasource.subscribe_mail.SubscribeEmailDataStoreFactory
import com.studita.domain.entity.SubscribeEmailResultData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.repository.SubscribeEmailRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SubscribeEmailRepositoryImpl(
    private val subscribeEmailDataStoreFactory: SubscribeEmailDataStoreFactory,
    private val subscribeEmailCache: SubscribeEmailCache
) : SubscribeEmailRepository {

    private val type: Type = object : TypeToken<SubscribeEmailResultEntity>() {}.type

    override suspend fun subscribe(userIdTokenData: UserIdTokenData): Pair<Int, SubscribeEmailResultData?> {
        val pair =
            subscribeEmailDataStoreFactory.create().trySubscribe(userIdTokenData.toRawEntity())
        return pair.first to pair.second?.toBusinessEntity()
    }

    override suspend fun unsubscribe(userIdTokenData: UserIdTokenData): Pair<Int, SubscribeEmailResultData?> {
        val pair =
            subscribeEmailDataStoreFactory.create().tryUnsubscribe(userIdTokenData.toRawEntity())
        return pair.first to pair.second?.toBusinessEntity()
    }

    override suspend fun saveSyncedResult(subscribeEmailResultData: SubscribeEmailResultData) {
        val json = Gson().toJson(subscribeEmailResultData.toRawEntity())
        subscribeEmailCache.saveSubscribeEmailJson(json)
    }

    override fun getSyncedResult(): SubscribeEmailResultData? {
        val json = subscribeEmailCache.getSubscribeEmailJson()
        subscribeEmailCache.deleteSubscribeEmailJson()
        return Gson().fromJson<SubscribeEmailResultEntity>(json, type)?.toBusinessEntity()
    }

}