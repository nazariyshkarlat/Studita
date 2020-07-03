package com.example.studita.domain.interactor.levels

import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.example.studita.domain.interactor.InterestingCacheStatus
import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.domain.interactor.LevelsCacheStatus
import com.example.studita.domain.interactor.PrivacySettingsDuelsExceptionsStatus
import com.example.studita.domain.repository.LevelsRepository
import kotlinx.coroutines.delay
import java.lang.Exception

class LevelsInteractorImpl(
    private val repository: LevelsRepository
): LevelsInteractor {

    private val retryDelay = 1000L

    override suspend fun getLevels(isLoggedIn: Boolean, offlineMode: Boolean, retryCount: Int): LevelsStatus =
         try {
             val results = repository.getLevels(isLoggedIn, offlineMode)
             LevelsStatus.Success(results)
         } catch (e: Exception) {
             e.printStackTrace()
             if(e is NetworkConnectionException || e is ServerUnavailableException) {
                 if (retryCount == 0) {
                     if (e is NetworkConnectionException) {
                         LevelsStatus.NoConnection
                     } else
                         LevelsStatus.ServiceUnavailable
                 } else {
                     if (e is NetworkConnectionException)
                         delay(retryDelay)
                     getLevels(isLoggedIn, offlineMode, retryCount - 1)
                 }
             }else
                 LevelsStatus.Failure
         }

    override suspend fun downloadLevels(retryCount: Int): LevelsCacheStatus =
        try {
            val result = repository.downloadLevels()
            if(result == 200)
                LevelsCacheStatus.Success
            else
                LevelsCacheStatus.IsCached
        } catch (e: Exception) {
            e.printStackTrace()
            if(e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        LevelsCacheStatus.NoConnection
                    } else
                        LevelsCacheStatus.ServiceUnavailable
                } else {
                    if (e is NetworkConnectionException)
                        delay(retryDelay)
                    downloadLevels(retryCount - 1)
                }
            }else
                LevelsCacheStatus.Failure
        }

}