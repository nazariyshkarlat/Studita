package com.studita.domain.interactor.levels

import com.studita.domain.entity.LevelChildData
import com.studita.domain.entity.LevelData
import com.studita.domain.entity.LevelsDataData
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.domain.interactor.LevelsCacheStatus
import com.studita.domain.interactor.LevelsStatus
import com.studita.domain.repository.LevelsRepository
import kotlinx.coroutines.delay

class LevelsInteractorImpl(
    private val repository: LevelsRepository
) : LevelsInteractor {

    private val retryDelay = 1000L

    override suspend fun getLevels(
        isLoggedIn: Boolean,
        offlineMode: Boolean,
        retryCount: Int
    ): LevelsStatus =
        try {
            val results = repository.getLevels(offlineMode)
            LevelsStatus.Success(results.map {LevelData(it.levelNumber,it.levelName, it.levelChildren)   })
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        LevelsStatus.NoConnection
                    } else
                        LevelsStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    getLevels(isLoggedIn, offlineMode, retryCount - 1)
                }
            } else
                LevelsStatus.Failure
        }

    override suspend fun downloadLevels(retryCount: Int): LevelsCacheStatus =
        try {
            when (repository.downloadLevels()) {
                200 -> LevelsCacheStatus.Success
                409 -> LevelsCacheStatus.IsCached
                else -> LevelsCacheStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        LevelsCacheStatus.NoConnection
                    } else
                        LevelsCacheStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    downloadLevels(retryCount - 1)
                }
            } else
                LevelsCacheStatus.Failure
        }

}