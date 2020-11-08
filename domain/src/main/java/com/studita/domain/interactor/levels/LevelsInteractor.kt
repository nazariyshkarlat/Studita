package com.studita.domain.interactor.levels

import com.studita.domain.interactor.LevelsCacheStatus
import com.studita.domain.interactor.LevelsStatus

interface LevelsInteractor {

    suspend fun getLevels(
        isLoggedIn: Boolean,
        offlineMode: Boolean,
        retryCount: Int = 3
    ): LevelsStatus

    suspend fun downloadLevels(retryCount: Int = 3): LevelsCacheStatus
}