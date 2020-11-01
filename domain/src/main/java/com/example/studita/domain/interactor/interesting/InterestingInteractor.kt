package com.example.studita.domain.interactor.interesting

import com.example.studita.domain.entity.InterestingLikeRequestData
import com.example.studita.domain.interactor.InterestingCacheStatus
import com.example.studita.domain.interactor.InterestingLikeStatus
import com.example.studita.domain.interactor.InterestingStatus

interface InterestingInteractor {

    suspend fun getInteresting(
        interestingNumber: Int,
        offlineMode: Boolean,
        retryCount: Int = Int.MAX_VALUE
    ): InterestingStatus

    suspend fun downloadInterestingList(retryCount: Int = 3): InterestingCacheStatus

    suspend fun sendInterestingLike(
        interestingLikeRequestData: InterestingLikeRequestData,
        retryCount: Int = 3
    ): InterestingLikeStatus

}