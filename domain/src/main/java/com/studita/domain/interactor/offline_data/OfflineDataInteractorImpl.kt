package com.studita.domain.interactor.offline_data

import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.domain.interactor.DownloadOfflineDataStatus
import com.studita.domain.interactor.LevelsCacheStatus
import com.studita.domain.repository.OfflineDataRepository
import kotlinx.coroutines.delay

open class OfflineDataInteractorImpl(private val repository: OfflineDataRepository) : OfflineDataInteractor{

    private val retryDelay = 1000L

    override suspend fun downloadOfflineData(retryCount: Int): DownloadOfflineDataStatus =
        try {
            when (repository.downloadOfflineData(this)) {
                200 -> DownloadOfflineDataStatus.Success
                409 -> DownloadOfflineDataStatus.IsCached
                else -> DownloadOfflineDataStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        DownloadOfflineDataStatus.NoConnection
                    } else
                        DownloadOfflineDataStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    downloadOfflineData(retryCount - 1)
                }
            } else
                DownloadOfflineDataStatus.Failure
        }

    override fun onDownload(percent: Float, totalSizeMb: Float, done: Boolean) {

    }

}