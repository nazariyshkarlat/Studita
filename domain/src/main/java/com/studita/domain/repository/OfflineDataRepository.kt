package com.studita.domain.repository

import com.studita.domain.interactor.offline_data.OfflineDataInteractor

interface OfflineDataRepository {

    suspend fun downloadOfflineData(offlineDataInteractor: OfflineDataInteractor) : Int

    fun onDownload(bytesRead: Long, contentLength: Long, done: Boolean)

}