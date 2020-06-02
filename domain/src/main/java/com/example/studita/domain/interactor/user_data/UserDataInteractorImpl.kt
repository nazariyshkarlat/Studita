package com.example.studita.domain.interactor.user_data

import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.domain.repository.UserDataRepository

class UserDataInteractorImpl(private val repository: UserDataRepository) : UserDataInteractor{
    override suspend fun getUserData(userId: Int?, offlineMode: Boolean): UserDataStatus =
        try {
            val result = repository.getUserData(userId, offlineMode)
            when (result.first) {
                200 -> UserDataStatus.Success(result.second)
                else -> UserDataStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                UserDataStatus.NoConnection
            else
                UserDataStatus.ServiceUnavailable
        }

    override suspend fun saveUserData(userDataData: UserDataData) {
        repository.saveUserData(userDataData)
    }

    override suspend fun deleteUserData() {
        repository.deleteUserData()
    }

}