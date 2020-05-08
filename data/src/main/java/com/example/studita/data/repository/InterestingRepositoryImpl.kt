package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.InterestingDataMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.chapter.ChapterJsonDataStoreFactory
import com.example.studita.data.repository.datasource.chapter.CloudChapterJsonDataStore
import com.example.studita.data.repository.datasource.chapter.DiskChapterJsonDataStore
import com.example.studita.data.repository.datasource.interesting.CloudInterestingJsonDataStore
import com.example.studita.data.repository.datasource.interesting.DiskInterestingJsonDataStore
import com.example.studita.data.repository.datasource.interesting.InterestingDataStoreImpl
import com.example.studita.data.repository.datasource.interesting.InterestingJsonDataStoreFactory
import com.example.studita.domain.entity.InterestingData
import com.example.studita.domain.repository.InterestingRepository

class InterestingRepositoryImpl(private val interestingJsonDataStoreFactory: InterestingJsonDataStoreFactory,
                                private val chapterDataMapper: InterestingDataMapper,
                                private val connectionManager: ConnectionManager
): InterestingRepository {

    override suspend fun getInteresting(interestingNumber: Int, offlineMode: Boolean): Pair<Int, InterestingData> =
        with(InterestingDataStoreImpl(interestingJsonDataStoreFactory.create(if(offlineMode || connectionManager.isNetworkAbsent()) InterestingJsonDataStoreFactory.Priority.CACHE else InterestingJsonDataStoreFactory.Priority.CLOUD)).getInterestingEntity(interestingNumber)){
            this.first to chapterDataMapper.map(this.second)
        }

    override suspend fun downloadInterestingList(): Int {
        val diskDataStore = (interestingJsonDataStoreFactory.create(InterestingJsonDataStoreFactory.Priority.CACHE) as DiskInterestingJsonDataStore)
        return if (!diskDataStore.interestingIsCached()) {
            val json =
                (interestingJsonDataStoreFactory.create(InterestingJsonDataStoreFactory.Priority.CLOUD) as CloudInterestingJsonDataStore).getInterestingListJson()
            diskDataStore.saveInterestingListJson(json)
            200
        } else
            409
    }
}