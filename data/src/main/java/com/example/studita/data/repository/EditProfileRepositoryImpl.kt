package com.example.studita.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.studita.data.entity.mapper.toFile
import com.example.studita.data.entity.toRawEntity
import com.example.studita.data.repository.datasource.edit_profile.EditProfileDataStoreFactory
import com.example.studita.domain.entity.EditProfileRequestData
import com.example.studita.domain.repository.EditProfileRepository

class EditProfileRepositoryImpl(private val editProfileDataStoreFactory: EditProfileDataStoreFactory, private val context: Context) : EditProfileRepository{
    override suspend fun editProfile(editProfileRequestData: EditProfileRequestData, newAvatar: Bitmap?): Pair<String?, Int> =
            editProfileDataStoreFactory.create().tryEditProfile(editProfileRequestData.toRawEntity(), newAvatar?.toFile(context))

    override suspend fun isUserNameAvailable(userName: String): Pair<Int, Boolean?> =
        editProfileDataStoreFactory.create().isUserNameAvailable(userName)
}