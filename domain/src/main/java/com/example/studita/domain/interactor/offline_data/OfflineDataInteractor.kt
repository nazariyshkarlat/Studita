package com.example.studita.domain.interactor.offline_data

import com.example.studita.domain.interactor.DownloadOfflineDataStatus

interface OfflineDataInteractor {

    suspend fun downloadOfflineData(retryCount: Int = 3) : DownloadOfflineDataStatus

    fun onDownload(percent: Float, totalSizeMb: Float, done: Boolean)

}