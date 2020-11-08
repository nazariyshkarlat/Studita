package com.studita.data.repository.datasource.interesting

import com.studita.data.cache.interesting.InterestingCache
import com.studita.domain.exception.NetworkConnectionException

class DiskInterestingJsonDataStore(
    private val interestingCache: InterestingCache
) : InterestingJsonDataStore {
    override suspend fun getInterestingJson(interestingNumber: Int): Pair<Int, String> {
        val json = interestingCache.getInterestingJson(interestingNumber)
        return 200 to (json ?: throw NetworkConnectionException())
    }

    fun interestingIsCached() = interestingCache.isCached(1)

    fun saveInterestingListJson(json: String) = interestingCache.saveInterestingListJson(json)
}