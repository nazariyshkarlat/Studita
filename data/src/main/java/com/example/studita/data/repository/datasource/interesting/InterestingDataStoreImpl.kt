package com.example.studita.data.repository.datasource.interesting

import com.example.studita.data.entity.ChapterEntity
import com.example.studita.data.entity.interesting.InterestingEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class InterestingDataStoreImpl(private val interestingJsonDataStore: InterestingJsonDataStore) : InterestingDataStore {

    private val type: Type = object : TypeToken<InterestingEntity>() {}.type

    override suspend fun getInterestingEntity(interestingNumber: Int): Pair<Int, InterestingEntity> {
        val pair = interestingJsonDataStore.getInterestingJson(interestingNumber)
        return pair.first to Gson().fromJson<InterestingEntity>(pair.second, type)
    }

}