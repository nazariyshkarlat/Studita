package com.studita.data.repository.datasource.interesting.result

import com.studita.data.entity.exercise.*
import com.studita.data.entity.interesting.InterestingLikeRequest
import com.studita.data.net.ExerciseResultService
import com.studita.data.net.InterestingResultService
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CancellationException
import java.lang.Exception
import java.lang.reflect.Type

class InterestingResultDataStoreImpl(
    private val connectionManager: ConnectionManager,
    private val interestingResultService: InterestingResultService
) : InterestingResultDataStore {

    override suspend fun sendInterestingLike(interestingLikeRequest: InterestingLikeRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                return interestingResultService.sendInterestingLike(interestingLikeRequest).code()
            }catch (e: Exception){
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }
}