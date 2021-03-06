package com.studita.data.repository.datasource.edit_profile

import com.studita.data.entity.EditProfileRequest
import com.studita.data.net.EditProfileService
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class EditProfileDataStoreImpl(
    private val connectionManager: ConnectionManager,
    private val editProfileService: EditProfileService
) : EditProfileDataStore {
    override suspend fun tryEditProfile(
        editProfileRequest: EditProfileRequest,
        newAvatarFile: File?
    ): Pair<String?, Int> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = editProfileService.editProfile(
                    editProfileRequest.toRequestBody(),
                    newAvatarFile?.toBodyPart()
                )
                val avatarLink = Json.decodeFromString<HashMap<String, String?>>(result.body().toString())["avatar_link"]
                avatarLink to result.code()
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }

    override suspend fun isUserNameAvailable(userName: String): Pair<Int, Boolean?> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val response = editProfileService.isUserNameAvailable(userName)
                response.code() to response.body()
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }

    private fun File.toBodyPart(): MultipartBody.Part {
        val reqFile = this.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("avatar", this.name, reqFile)
    }

    private fun EditProfileRequest.toRequestBody() =
        Json.encodeToString(this).toRequestBody("application/json".toMediaTypeOrNull())


}