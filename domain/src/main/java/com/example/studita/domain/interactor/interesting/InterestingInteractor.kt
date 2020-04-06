package com.example.studita.domain.interactor.interesting

import com.example.studita.domain.interactor.ChapterStatus
import com.example.studita.domain.interactor.InterestingStatus

interface InterestingInteractor {

    suspend fun getInteresting(interestingNumber: Int) : InterestingStatus

}