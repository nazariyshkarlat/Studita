package com.studita.domain.repository

import com.studita.domain.entity.InterestingData
import com.studita.domain.entity.InterestingDataScreen
import com.studita.domain.entity.InterestingLikeRequestData

interface InterestingRepository {

    suspend fun getInteresting(
        interestingNumber: Int,
        offlineMode: Boolean
    ): Pair<Int, InterestingData>

    suspend fun downloadInterestingList(): Int

    suspend fun sendInterestingLike(interestingLikeRequestData: InterestingLikeRequestData): Int

}