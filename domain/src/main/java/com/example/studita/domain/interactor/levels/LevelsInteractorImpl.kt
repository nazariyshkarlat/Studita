package com.example.studita.domain.interactor.levels

import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.domain.interactor.LevelsCacheStatus
import com.example.studita.domain.repository.LevelsRepository
import java.lang.Exception

class LevelsInteractorImpl(
    private val repository: LevelsRepository
): LevelsInteractor {

     override suspend fun getLevels(isLoggedIn: Boolean): LevelsStatus =
         try {
             val results = repository.getLevels(isLoggedIn)
             LevelsStatus.Success(results)
         } catch (e: Exception) {
             e.printStackTrace()
             if (e is NetworkConnectionException)
                 LevelsStatus.NoConnection
             else
                 LevelsStatus.ServiceUnavailable
         }

    override suspend fun downloadLevels(): LevelsCacheStatus =
        try {
            val result = repository.downloadLevels()
            if(result == 200)
                LevelsCacheStatus.Success
            else
                LevelsCacheStatus.IsCached
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                LevelsCacheStatus.NoConnection
            else
                LevelsCacheStatus.ServiceUnavailable
        }

}