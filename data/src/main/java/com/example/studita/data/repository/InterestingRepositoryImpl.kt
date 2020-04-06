package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.InterestingDataMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.interesting.InterestingDataStoreImpl
import com.example.studita.data.repository.datasource.interesting.InterestingJsonDataStoreFactory
import com.example.studita.domain.entity.InterestingData
import com.example.studita.domain.repository.InterestingRepository

class InterestingRepositoryImpl(private val chapterJsonDataStoreFactory: InterestingJsonDataStoreFactory,
                                private val chapterDataMapper: InterestingDataMapper,
                                private val connectionManager: ConnectionManager
): InterestingRepository {

    override suspend fun getInteresting(interestingNumber: Int): Pair<Int, InterestingData> =
        with(InterestingDataStoreImpl(chapterJsonDataStoreFactory.create(if(connectionManager.isNetworkAbsent()) InterestingJsonDataStoreFactory.Priority.CACHE else InterestingJsonDataStoreFactory.Priority.CLOUD)).getInterestingEntity(interestingNumber)){
            this.first to chapterDataMapper.map(this.second)
        }
}