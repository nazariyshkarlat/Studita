package com.studita.domain.interactor.interesting

import com.studita.domain.entity.InterestingLikeRequestData
import com.studita.domain.interactor.InterestingCacheStatus
import com.studita.domain.interactor.InterestingLikeStatus
import com.studita.domain.interactor.InterestingStatus

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