package com.studita.data.repository

import com.studita.data.cache.subscribe_email.SubscribeEmailCache
import com.studita.data.entity.SubscribeEmailResultEntity
import com.studita.data.entity.toBusinessEntity
import com.studita.data.entity.toRawEntity
import com.studita.data.repository.datasource.subscribe_mail.SubscribeEmailDataStoreFactory
import com.studita.domain.entity.SubscribeEmailResultData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.repository.SubscribeEmailRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.reflect.Type

class SubscribeEmailRepositoryImpl(
    private val subscribeEmailDataStoreFactory: SubscribeEmailDataStoreFactory,
    private val subscribeEmailCache: SubscribeEmailCache
) : SubscribeEmailRepository {

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
        val json = Json.encodeToString(subscribeEmailResultData.toRawEntity())
        subscribeEmailCache.saveSubscribeEmailJson(json)
    }

    override fun getSyncedResult(): SubscribeEmailResultData? {
        val json = subscribeEmailCache.getSubscribeEmailJson()
        subscribeEmailCache.deleteSubscribeEmailJson()
        return json?.let { Json.decodeFromString<SubscribeEmailResultEntity>(it).toBusinessEntity() }
    }

}