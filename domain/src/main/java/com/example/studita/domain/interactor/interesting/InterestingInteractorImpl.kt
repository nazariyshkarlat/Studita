package com.example.studita.domain.interactor.interesting

import com.example.studita.domain.entity.InterestingLikeRequestData
import com.example.studita.domain.entity.exercise.ExerciseReportRequestData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.example.studita.domain.interactor.*
import com.example.studita.domain.repository.InterestingRepository
import com.example.studita.domain.service.SyncInterestingLikes
import kotlinx.coroutines.delay

class InterestingInteractorImpl(
    private val repository: InterestingRepository,
    private val syncInterestingLikes: SyncInterestingLikes
) : InterestingInteractor {

    private val retryDelay = 1000L

    override suspend fun getInteresting(
        interestingNumber: Int,
        offlineMode: Boolean,
        retryCount: Int
    ): InterestingStatus =
        try {
            val results = repository.getInteresting(interestingNumber, offlineMode)

            if (results.first == 200)
                InterestingStatus.Success(results.second)
            else
                InterestingStatus.NoInterestingFound

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        InterestingStatus.NoConnection
                    } else
                        InterestingStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    getInteresting(interestingNumber, offlineMode, retryCount - 1)
                }
            } else
                InterestingStatus.Failure
        }

    override suspend fun downloadInterestingList(retryCount: Int): InterestingCacheStatus =
        try {
            val result = repository.downloadInterestingList()
            if (result == 200)
                InterestingCacheStatus.Success
            else
                InterestingCacheStatus.IsCached
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        InterestingCacheStatus.NoConnection
                    } else
                        InterestingCacheStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    downloadInterestingList(retryCount - 1)
                }
            } else
                InterestingCacheStatus.Failure
        }

    override suspend fun sendInterestingLike(
        interestingLikeRequestData: InterestingLikeRequestData,
        retryCount: Int
    ) : InterestingLikeStatus =
        try {
            if (repository.sendInterestingLike(interestingLikeRequestData) == 200)
                InterestingLikeStatus.Success
            else
                InterestingLikeStatus.Failure
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if(e is NetworkConnectionException) {
                        syncInterestingLikes.scheduleSendInterestingLike(
                            interestingLikeRequestData
                        )
                        InterestingLikeStatus.NoConnection
                    }else
                        InterestingLikeStatus.ServiceUnavailable
                }
                else {
                    delay(retryDelay)
                    sendInterestingLike(interestingLikeRequestData, retryCount - 1)
                }
            } else
                InterestingLikeStatus.Failure
        }
}