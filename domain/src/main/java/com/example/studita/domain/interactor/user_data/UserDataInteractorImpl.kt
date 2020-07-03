package com.example.studita.domain.interactor.user_data

import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.domain.interactor.UserStatisticsStatus
import com.example.studita.domain.repository.UserDataRepository
import kotlinx.coroutines.delay

class UserDataInteractorImpl(private val repository: UserDataRepository) : UserDataInteractor{

    private val retryDelay = 1000L

    override suspend fun getUserData(userId: Int?, offlineMode: Boolean, retryCount: Int): UserDataStatus =
        try {
            val result = repository.getUserData(userId, offlineMode)
            when (result.first) {
                200 -> UserDataStatus.Success(result.second)
                else -> UserDataStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if(e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        UserDataStatus.NoConnection
                    } else
                        UserDataStatus.ServiceUnavailable
                } else {
                    if (e is NetworkConnectionException)
                        delay(retryDelay)
                    getUserData(userId, offlineMode, retryCount - 1)
                }
            }else
                UserDataStatus.Failure
        }

    override suspend fun saveUserData(userDataData: UserDataData) {
        repository.saveUserData(userDataData)
    }

    override suspend fun deleteUserData() {
        repository.deleteUserData()
    }

}