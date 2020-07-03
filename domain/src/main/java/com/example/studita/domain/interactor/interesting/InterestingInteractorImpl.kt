package com.example.studita.domain.interactor.interesting

import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.example.studita.domain.interactor.ChaptersCacheStatus
import com.example.studita.domain.interactor.InterestingCacheStatus
import com.example.studita.domain.interactor.InterestingStatus
import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.domain.repository.InterestingRepository
import kotlinx.coroutines.delay
import java.lang.Exception

class InterestingInteractorImpl(
    private val repository: InterestingRepository
): InterestingInteractor {

    private val retryDelay = 1000L

    override suspend fun getInteresting(interestingNumber: Int, offlineMode: Boolean, retryCount: Int): InterestingStatus =
        try {
            val results = repository.getInteresting(interestingNumber, offlineMode)

            if(results.first == 200)
                InterestingStatus.Success(results.second)
            else
                InterestingStatus.NoInterestingFound

        } catch (e: Exception) {
            e.printStackTrace()
            if(e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        InterestingStatus.NoConnection
                    } else
                        InterestingStatus.ServiceUnavailable
                } else {
                    if (e is NetworkConnectionException)
                        delay(retryDelay)
                    getInteresting(interestingNumber, offlineMode, retryCount - 1)
                }
            }else
                InterestingStatus.Failure
        }

    override suspend fun downloadInterestingList(retryCount: Int): InterestingCacheStatus =
        try {
            val result = repository.downloadInterestingList()
            if(result == 200)
                InterestingCacheStatus.Success
            else
                InterestingCacheStatus.IsCached
        } catch (e: Exception) {
            e.printStackTrace()
            if(e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        InterestingCacheStatus.NoConnection
                    } else
                        InterestingCacheStatus.ServiceUnavailable
                } else {
                    if (e is NetworkConnectionException)
                        delay(retryDelay)
                    downloadInterestingList(retryCount - 1)
                }
            }else
                InterestingCacheStatus.Failure
        }
}