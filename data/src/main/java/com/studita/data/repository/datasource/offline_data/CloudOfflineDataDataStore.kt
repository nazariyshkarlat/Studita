package com.studita.data.repository.datasource.offline_data

import com.studita.data.entity.OfflineDataEntity
import com.studita.data.net.OfflineDataService
import com.studita.data.net.progress.ProgressResponseBody
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.domain.repository.OfflineDataRepository
import kotlinx.coroutines.CancellationException

class CloudOfflineDataDataStore(private val connectionManager: ConnectionManager,
                                private val offlineDataService: OfflineDataService) : ProgressResponseBody.ProgressListener {

    lateinit var offlineDataRepository: OfflineDataRepository

    suspend fun getOfflineDataJson(offlineDataRepository: OfflineDataRepository): Pair<Int, OfflineDataEntity> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                this.offlineDataRepository = offlineDataRepository
                val result = offlineDataService.getOfflineData()
                val body = result.body()!!
                result.code() to OfflineDataEntity(body["levels"].toString(), body["chapters"].toString(), body["offline_exercises"].toString())
            } catch (e: Exception) {
                e.printStackTrace()
                when {
                    connectionManager.isNetworkAbsent() -> throw NetworkConnectionException()
                    e is CancellationException -> throw e
                    else -> throw ServerUnavailableException()
                }
            }
    }

    override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
        offlineDataRepository.onDownload(bytesRead, contentLength, done)
    }


}