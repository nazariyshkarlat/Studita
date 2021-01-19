package com.studita.data.repository.datasource.achievements

import com.studita.data.entity.AchievementDataEntity
import com.studita.data.entity.AchievementEntity
import com.studita.data.net.AchievementsService
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception
import kotlin.coroutines.cancellation.CancellationException

class AchievementsDataStoreImpl(
    private val achievementsService: AchievementsService,
    private val connectionManager: ConnectionManager) : AchievementsDataStore {
    override fun getAchievements(userId: Int): Flow<List<AchievementEntity>> = flow {
        check(!connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }
        try {
            emit(achievementsService.getAchievements(userId).body()!!)
        }catch (e: Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
            else throw ServerUnavailableException()
        }
    }

    override fun getAchievementsData(userId: Int?): Flow<List<AchievementDataEntity>> = flow {
        check(!connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }
        try {
            println("get achievements dasdasd")
            emit(achievementsService.getAchievementsData(userId).body()!!)
        }catch (e: Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
            else throw ServerUnavailableException()
        }
    }
}