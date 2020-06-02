package com.example.studita.domain.repository

import android.graphics.Bitmap
import com.example.studita.domain.entity.EditProfileRequestData

interface EditProfileRepository {

    suspend fun editProfile(editProfileRequestData: EditProfileRequestData, newAvatar: Bitmap?): Pair<String?, Int>

    suspend fun isUserNameAvailable(userName: String): Pair<Int, Boolean?>

}