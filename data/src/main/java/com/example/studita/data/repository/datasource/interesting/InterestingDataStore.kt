package com.example.studita.data.repository.datasource.interesting

import com.example.studita.data.entity.ChapterEntity
import com.example.studita.data.entity.interesting.InterestingEntity

interface InterestingDataStore{
    suspend fun getInterestingEntity(interestingNumber: Int): Pair<Int, InterestingEntity>
}