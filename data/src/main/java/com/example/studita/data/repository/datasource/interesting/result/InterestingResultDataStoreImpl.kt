package com.example.studita.data.repository.datasource.interesting.result

import com.example.studita.data.entity.exercise.*
import com.example.studita.data.entity.interesting.InterestingLikeRequest
import com.example.studita.data.net.ExerciseResultService
import com.example.studita.data.net.InterestingResultService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
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
                throw ServerUnavailableException()
            }
        }
    }
}