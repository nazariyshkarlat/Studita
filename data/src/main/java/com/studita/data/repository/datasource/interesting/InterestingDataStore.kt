package com.studita.data.repository.datasource.interesting

import com.studita.data.entity.interesting.InterestingEntity

interface InterestingDataStore {
    suspend fun getInterestingEntity(interestingNumber: Int): Pair<Int, InterestingEntity>
}