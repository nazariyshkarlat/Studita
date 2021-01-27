package com.studita.data.repository

import com.studita.data.repository.datasource.offline_data.CloudOfflineDataDataStore
import com.studita.data.repository.datasource.offline_data.DiskOfflineDataDataStore
import com.studita.domain.interactor.offline_data.OfflineDataInteractor
import com.studita.domain.repository.OfflineDataRepository

class OfflineDataRepositoryImpl(
    private val cloudOfflineDataDataStore: CloudOfflineDataDataStore,
    private val diskOfflineDataDataStore: DiskOfflineDataDataStore
) : OfflineDataRepository{

    lateinit var offlineDataInteractor: OfflineDataInteractor

    override suspend fun downloadOfflineData(offlineDataInteractor: OfflineDataInteractor): Int {

        this.offlineDataInteractor = offlineDataInteractor

        var code = 409
        if (!diskOfflineDataDataStore.offlineDataIsCached()) {
            val offlineDataPair = cloudOfflineDataDataStore.getOfflineDataJson(this)
            diskOfflineDataDataStore.saveOfflineDataJson(offlineDataPair.second.levelsData, offlineDataPair.second.chaptersData, offlineDataPair.second.exercisesData)
            code = offlineDataPair.first
        }
        return code
    }

    override fun onDownload(bytesRead: Long, contentLength: Long, done: Boolean) {
        offlineDataInteractor.onDownload(bytesRead.toFloat()/contentLength, contentLength.toFloat()/1024/1024, done)
    }

}