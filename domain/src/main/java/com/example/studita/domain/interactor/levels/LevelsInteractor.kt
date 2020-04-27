package com.example.studita.domain.interactor.levels

import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.domain.interactor.LevelsCacheStatus

interface LevelsInteractor{

    suspend fun getLevels(isLoggedIn: Boolean): LevelsStatus

    suspend fun downloadLevels(): LevelsCacheStatus
    }