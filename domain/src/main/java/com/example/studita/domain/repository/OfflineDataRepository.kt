package com.example.studita.domain.repository

import com.example.studita.domain.interactor.offline_data.OfflineDataInteractor

interface OfflineDataRepository {

    suspend fun downloadOfflineData(offlineDataInteractor: OfflineDataInteractor) : Int

    fun onDownload(bytesRead: Long, contentLength: Long, done: Boolean)

}