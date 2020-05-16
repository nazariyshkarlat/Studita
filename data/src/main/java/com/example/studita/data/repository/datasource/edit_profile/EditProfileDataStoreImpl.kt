package com.example.studita.data.repository.datasource.edit_profile

import com.example.studita.data.entity.EditProfileRequest
import com.example.studita.data.net.EditProfileService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException

class EditProfileDataStoreImpl(private val connectionManager: ConnectionManager, private val editProfileService: EditProfileService) : EditProfileDataStore{
    override suspend fun tryEditProfile(editProfileRequest: EditProfileRequest): Int =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                editProfileService.editProfile(editProfileRequest).code()
            }catch (e: Exception){
                throw ServerUnavailableException()
            }
    }


}