package com.studita.domain.interactor.user_data

import com.studita.domain.entity.UserDataData
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.domain.interactor.UserDataStatus
import com.studita.domain.repository.UserDataRepository
import kotlinx.coroutines.delay

class UserDataInteractorImpl(private val repository: UserDataRepository) : UserDataInteractor {

    private val retryDelay = 1000L

    override suspend fun getUserData(
        userId: Int?,
        getFromLocalStorage: Boolean,
        isMyUserData: Boolean,
        retryCount: Int
    ): UserDataStatus =
        try {
            val result = repository.getUserData(userId, getFromLocalStorage, isMyUserData)
            when (result.first) {
                200 -> UserDataStatus.Success(result.second)
                else -> UserDataStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        UserDataStatus.NoConnection
                    } else
                        UserDataStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    getUserData(userId, getFromLocalStorage, isMyUserData,retryCount - 1)
                }
            } else
                UserDataStatus.Failure
        }

    override suspend fun saveUserData(userDataData: UserDataData) {
        repository.saveUserData(userDataData)
    }

    override suspend fun deleteUserData() {
        repository.deleteUserData()
    }

}