package com.studita.data.repository.datasource.interesting

import com.studita.data.net.InterestingListService
import com.studita.data.net.InterestingService
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException

class CloudInterestingJsonDataStore(
    private val connectionManager: ConnectionManager,
    private val interestingService: InterestingService,
    private val interestingListService: InterestingListService
) : InterestingJsonDataStore {

    override suspend fun getInterestingJson(interestingNumber: Int): Pair<Int, String> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val interesting = interestingService.getInteresting(interestingNumber)
                return interesting.code() to interesting.body()!!.toString()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }


    suspend fun getInterestingListJson(): String {
        try {
            if (connectionManager.isNetworkAbsent()) {
                throw NetworkConnectionException()
            } else {
                val interestingList = interestingListService.getInterestingList()
                return interestingList.body()!!.toString()
            }
        } catch (e: Exception) {
            if (e is CancellationException)
                throw e
            else
                throw ServerUnavailableException()
        }
    }

}