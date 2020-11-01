package com.example.studita.domain.service

import com.example.studita.domain.entity.InterestingLikeRequestData

interface SyncInterestingLikes{

    fun scheduleSendInterestingLike(interestingLikeRequestData: InterestingLikeRequestData)

}