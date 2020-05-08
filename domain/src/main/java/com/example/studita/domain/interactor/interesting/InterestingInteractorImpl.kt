package com.example.studita.domain.interactor.interesting

import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.ChaptersCacheStatus
import com.example.studita.domain.interactor.InterestingCacheStatus
import com.example.studita.domain.interactor.InterestingStatus
import com.example.studita.domain.repository.InterestingRepository
import java.lang.Exception

class InterestingInteractorImpl(
    private val repository: InterestingRepository
): InterestingInteractor {

    override suspend fun getInteresting(interestingNumber: Int, offlineMode: Boolean): InterestingStatus =
        try {
            val results = repository.getInteresting(interestingNumber, offlineMode)

            if(results.first == 200)
                InterestingStatus.Success(results.second)
            else
                InterestingStatus.NoInterestingFound

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                InterestingStatus.NoConnection
            else
                InterestingStatus.ServiceUnavailable
        }

    override suspend fun downloadInterestingList(): InterestingCacheStatus =
        try {
            val result = repository.downloadInterestingList()
            if(result == 200)
                InterestingCacheStatus.Success
            else
                InterestingCacheStatus.IsCached
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                InterestingCacheStatus.NoConnection
            else
                InterestingCacheStatus.ServiceUnavailable
        }
}