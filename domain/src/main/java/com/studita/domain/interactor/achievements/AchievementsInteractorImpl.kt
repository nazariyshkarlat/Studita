package com.studita.domain.interactor.achievements

import com.studita.domain.entity.AchievementData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.studita.domain.interactor.GetAchievementsDataStatus
import com.studita.domain.interactor.GetAchievementsStatus
import com.studita.domain.interactor.UserDataStatus
import com.studita.domain.repository.AchievementsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class AchievementsInteractorImpl(private val achievementsRepository: AchievementsRepository) : AchievementsInteractor {

    val retryDelay = 1000L

    override fun getAchievements(userId: Int): Flow<GetAchievementsStatus> = achievementsRepository.getAchievements(userId).map {
        if(it.isNotEmpty())
            GetAchievementsStatus.Success(it)
        else
            GetAchievementsStatus.NoAchievements
    }.retry(3) {e ->
        e.printStackTrace()
        (e is NetworkConnectionException || e is ServerUnavailableException).also {
            if(it) delay(retryDelay)
        }
    }.catch { e ->
        e.printStackTrace()
        emit(
            when (e) {
                is NetworkConnectionException -> GetAchievementsStatus.NoConnection
                is ServerUnavailableException -> GetAchievementsStatus.ServiceUnavailable
                else -> GetAchievementsStatus.Failure
            }
        )
    }

    override fun getAchievementsData(userId: Int?): Flow<GetAchievementsDataStatus> =
        achievementsRepository.getAchievementsData(userId).map {
        GetAchievementsDataStatus.Success(it) as GetAchievementsDataStatus
    }.retry(3) {e ->
        e.printStackTrace()
        (e is NetworkConnectionException || e is ServerUnavailableException).also {
            if(it) delay(retryDelay)
        }
    }.catch { e ->
        e.printStackTrace()
        emit(
            when (e) {
                is NetworkConnectionException -> GetAchievementsDataStatus.NoConnection
                is ServerUnavailableException -> GetAchievementsDataStatus.ServiceUnavailable
                else -> GetAchievementsDataStatus.Failure
            }
        )
    }
}