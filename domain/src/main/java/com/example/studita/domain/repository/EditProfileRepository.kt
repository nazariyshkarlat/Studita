package com.example.studita.domain.repository

import com.example.studita.domain.entity.EditProfileRequestData

interface EditProfileRepository {

    suspend fun editProfile(editProfileRequestData: EditProfileRequestData): Int

}