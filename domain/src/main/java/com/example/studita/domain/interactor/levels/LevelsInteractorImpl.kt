package com.example.studita.domain.interactor.levels

import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.domain.repository.LevelsRepository
import java.lang.Exception

class LevelsInteractorImpl(
    private val repository: LevelsRepository
): LevelsInteractor {

     override suspend fun getLevels(): LevelsStatus =
        try {
            val results = repository.getLevels()
            LevelsStatus.Success(results)
        } catch (e: Exception) {
        e.printStackTrace()
            if (e is NetworkConnectionException)
                LevelsStatus.NoConnection
            else
                LevelsStatus.ServiceUnavailable
        }

}