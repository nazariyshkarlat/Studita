package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.EditProfileRequestMapper
import com.example.studita.data.repository.datasource.edit_profile.EditProfileDataStoreFactory
import com.example.studita.domain.entity.EditProfileRequestData
import com.example.studita.domain.repository.EditProfileRepository

class EditProfileRepositoryImpl(private val editProfileDataStoreFactory: EditProfileDataStoreFactory, private val editProfileRequestMapper: EditProfileRequestMapper) : EditProfileRepository{
    override suspend fun editProfile(editProfileRequestData: EditProfileRequestData): Int =
            editProfileDataStoreFactory.create().tryEditProfile(editProfileRequestMapper.map(editProfileRequestData))
}