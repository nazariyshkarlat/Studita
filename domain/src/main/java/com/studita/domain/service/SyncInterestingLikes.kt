package com.studita.domain.service

import com.studita.domain.entity.InterestingLikeRequestData

interface SyncInterestingLikes{

    fun scheduleSendInterestingLike(interestingLikeRequestData: InterestingLikeRequestData)

}