package com.studita.data.repository.datasource.subscribe_mail

import com.studita.data.entity.SubscribeEmailResultEntity
import com.studita.data.entity.UserIdToken

interface SubscribeEmailDataStore {

    suspend fun trySubscribe(userIdToken: UserIdToken): Pair<Int, SubscribeEmailResultEntity?>

    suspend fun tryUnsubscribe(userIdToken: UserIdToken): Pair<Int, SubscribeEmailResultEntity?>

}