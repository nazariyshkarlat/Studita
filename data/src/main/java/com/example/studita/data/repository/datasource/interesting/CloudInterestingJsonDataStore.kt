package com.example.studita.data.repository.datasource.interesting

import com.example.studita.data.cache.interesting.InterestingCache
import com.example.studita.data.net.InterestingService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException

class CloudInterestingJsonDataStore(
    private val connectionManager: ConnectionManager,
    private val interestingService: InterestingService,
    private val interestingCache: InterestingCache
)  : InterestingJsonDataStore{

    override suspend fun getInterestingJson(interestingNumber: Int): Pair<Int, String> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            val chapterAsync = interestingService.getInterestingAsync(interestingNumber)
            val result = chapterAsync.await()
            interestingCache.saveInterestingJson(interestingNumber, result.body()!!.toString())
            return result.code() to result.body()!!.toString()
        }
    }
}