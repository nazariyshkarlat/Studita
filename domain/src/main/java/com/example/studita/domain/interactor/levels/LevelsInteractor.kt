package com.example.studita.domain.interactor.levels

import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.domain.interactor.LevelsCacheStatus

interface LevelsInteractor{

    suspend fun getLevels(isLoggedIn: Boolean, offlineMode: Boolean, retryCount: Int = 30): LevelsStatus

    suspend fun downloadLevels(retryCount: Int = 30): LevelsCacheStatus
    }