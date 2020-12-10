package com.studita.data.repository

import com.studita.data.entity.interesting.InterestingLikeRequest
import com.studita.data.entity.interesting.toBusinessEntity
import com.studita.data.entity.interesting.toRawEntity
import com.studita.data.net.connection.ConnectionManager
import com.studita.data.repository.datasource.interesting.CloudInterestingJsonDataStore
import com.studita.data.repository.datasource.interesting.DiskInterestingJsonDataStore
import com.studita.data.repository.datasource.interesting.InterestingDataStoreImpl
import com.studita.data.repository.datasource.interesting.InterestingJsonDataStoreFactory
import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStore
import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStoreFactory
import com.studita.domain.entity.InterestingData
import com.studita.domain.entity.InterestingDataScreen
import com.studita.domain.entity.InterestingLikeRequestData
import com.studita.domain.repository.InterestingRepository

class InterestingRepositoryImpl(
    private val interestingJsonDataStoreFactory: InterestingJsonDataStoreFactory,
    private val interestingResultDataStoreFactory: InterestingResultDataStoreFactory,
    private val connectionManager: ConnectionManager
) : InterestingRepository {

    override suspend fun getInteresting(
        interestingNumber: Int,
        offlineMode: Boolean
    ): Pair<Int, InterestingData> =
        with(
            InterestingDataStoreImpl(interestingJsonDataStoreFactory.create(if (offlineMode) InterestingJsonDataStoreFactory.Priority.CACHE else InterestingJsonDataStoreFactory.Priority.CLOUD)).getInterestingEntity(
                interestingNumber
            )
        ) {
            this.first to this.second.toBusinessEntity()
        }

    override suspend fun downloadInterestingList(): Int {
        val diskDataStore =
            (interestingJsonDataStoreFactory.create(InterestingJsonDataStoreFactory.Priority.CACHE) as DiskInterestingJsonDataStore)
        return if (!diskDataStore.interestingIsCached()) {
            val json =
                (interestingJsonDataStoreFactory.create(InterestingJsonDataStoreFactory.Priority.CLOUD) as CloudInterestingJsonDataStore).getInterestingListJson()
            diskDataStore.saveInterestingListJson(json)
            200
        } else
            409
    }

    override suspend fun sendInterestingLike(interestingLikeRequestData: InterestingLikeRequestData): Int {
       return interestingResultDataStoreFactory.create().sendInterestingLike(interestingLikeRequestData.toRawEntity())
    }
}