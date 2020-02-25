package com.example.studita.domain.interactor.levels

import com.example.studita.domain.interactor.LevelsStatus

interface LevelsInteractor{

    suspend fun getLevels(): LevelsStatus

}