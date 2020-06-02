package com.example.studita.data.repository

import com.example.studita.data.cache.subscribe_email.SubscribeEmailCache
import com.example.studita.data.entity.ChapterEntity
import com.example.studita.data.entity.SubscribeEmailResultEntity
import com.example.studita.data.entity.mapper.SubscribeEmailDataMapper
import com.example.studita.data.entity.mapper.SubscribeEmailEntityMapper
import com.example.studita.data.entity.mapper.UserIdTokenMapper
import com.example.studita.data.repository.datasource.subscribe_mail.SubscribeEmailDataStoreFactory
import com.example.studita.domain.entity.SubscribeEmailResultData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.repository.SubscribeEmailRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SubscribeEmailRepositoryImpl(private val subscribeEmailDataStoreFactory: SubscribeEmailDataStoreFactory, private val subscribeEmailCache: SubscribeEmailCache, private val subscribeEmailDataMapper: SubscribeEmailDataMapper) : SubscribeEmailRepository{

    private val type: Type = object : TypeToken<SubscribeEmailResultEntity>() {}.type

    override suspend fun subscribe(userIdTokenData: UserIdTokenData): Pair<Int, SubscribeEmailResultData?>{
        val pair = subscribeEmailDataStoreFactory.create().trySubscribe(UserIdTokenMapper().map(userIdTokenData))
        return pair.first to pair.second?.let { subscribeEmailDataMapper.map(it) }
    }

    override suspend fun unsubscribe(userIdTokenData: UserIdTokenData): Pair<Int, SubscribeEmailResultData?> {
        val pair = subscribeEmailDataStoreFactory.create().tryUnsubscribe(UserIdTokenMapper().map(userIdTokenData))
        return pair.first to pair.second?.let { subscribeEmailDataMapper.map(it) }
    }

    override suspend fun saveSyncedResult(subscribeEmailResultData: SubscribeEmailResultData) {
        val json = Gson().toJson(SubscribeEmailEntityMapper().map(subscribeEmailResultData))
        subscribeEmailCache.saveSubscribeEmailJson(json)
    }

    override fun getSyncedResult(): SubscribeEmailResultData? {
        val json = subscribeEmailCache.getSubscribeEmailJson()
        subscribeEmailCache.deleteSubscribeEmailJson()
        val entity: SubscribeEmailResultEntity? = Gson().fromJson<SubscribeEmailResultEntity>(json, type)
        return if(entity != null) subscribeEmailDataMapper.map(entity) else null
    }

}