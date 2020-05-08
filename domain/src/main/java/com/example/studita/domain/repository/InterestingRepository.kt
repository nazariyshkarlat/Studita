package com.example.studita.domain.repository

import com.example.studita.domain.entity.ChapterData
import com.example.studita.domain.entity.InterestingData

interface InterestingRepository {

    suspend fun getInteresting(interestingNumber: Int, offlineMode: Boolean): Pair<Int, InterestingData>

    suspend fun downloadInterestingList(): Int

}