package com.example.studita.data.repository.datasource.interesting

import com.example.studita.data.net.InterestingListService
import com.example.studita.data.net.InterestingService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import java.lang.Exception

class CloudInterestingJsonDataStore(
    private val connectionManager: ConnectionManager,
    private val interestingService: InterestingService,
    private val interestingListService: InterestingListService
)  : InterestingJsonDataStore{

    override suspend fun getInterestingJson(interestingNumber: Int): Pair<Int, String> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            try {
                val interesting = interestingService.getInteresting(interestingNumber)
                return interesting.code() to interesting.body()!!.toString()
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }
    }


    suspend fun getInterestingListJson() : String{
        try {
            if (connectionManager.isNetworkAbsent()) {
                throw NetworkConnectionException()
            } else {
                val interestingList = interestingListService.getInterestingList()
                return interestingList.body()!!.toString()
            }
        }catch (e: Exception){
            throw ServerUnavailableException()
        }
    }

}