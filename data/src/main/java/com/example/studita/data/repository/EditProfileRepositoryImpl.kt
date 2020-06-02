package com.example.studita.data.repository

import android.graphics.Bitmap
import com.example.studita.data.entity.mapper.BitmapToFileMapper
import com.example.studita.data.entity.mapper.EditProfileRequestMapper
import com.example.studita.data.repository.datasource.edit_profile.EditProfileDataStoreFactory
import com.example.studita.domain.entity.EditProfileRequestData
import com.example.studita.domain.repository.EditProfileRepository

class EditProfileRepositoryImpl(private val editProfileDataStoreFactory: EditProfileDataStoreFactory, private val editProfileRequestMapper: EditProfileRequestMapper, private val bitmapToFileMapper: BitmapToFileMapper) : EditProfileRepository{
    override suspend fun editProfile(editProfileRequestData: EditProfileRequestData, newAvatar: Bitmap?): Pair<String?, Int> =
            editProfileDataStoreFactory.create().tryEditProfile(editProfileRequestMapper.map(editProfileRequestData), newAvatar?.let { bitmapToFileMapper.map(it) })

    override suspend fun isUserNameAvailable(userName: String): Pair<Int, Boolean?> =
        editProfileDataStoreFactory.create().isUserNameAvailable(userName)
}