package com.example.studita.domain.interactor.interesting

import com.example.studita.domain.interactor.ChapterStatus
import com.example.studita.domain.interactor.ChaptersCacheStatus
import com.example.studita.domain.interactor.InterestingCacheStatus
import com.example.studita.domain.interactor.InterestingStatus

interface InterestingInteractor {

    suspend fun getInteresting(interestingNumber: Int, offlineMode: Boolean, retryCount: Int = 30) : InterestingStatus

    suspend fun downloadInterestingList(retryCount: Int = 30): InterestingCacheStatus

}