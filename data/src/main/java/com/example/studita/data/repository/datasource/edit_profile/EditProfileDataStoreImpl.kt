package com.example.studita.data.repository.datasource.edit_profile

import com.example.studita.data.entity.EditProfileRequest
import com.example.studita.data.net.EditProfileService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class EditProfileDataStoreImpl(private val connectionManager: ConnectionManager, private val editProfileService: EditProfileService) : EditProfileDataStore{
    override suspend fun tryEditProfile(editProfileRequest: EditProfileRequest, newAvatarFile: File?): Pair<String?, Int> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = editProfileService.editProfile(editProfileRequest.toRequestBody(), newAvatarFile?.toBodyPart())
                println(result.body())
                val avatarLink = Gson().fromJson<HashMap<String, String?>>(result.body().toString(), object : TypeToken<HashMap<String, String?>>(){}.type)["avatar_link"]
                avatarLink to result.code()
            }catch (e: Exception){
                if(e is CancellationException)
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
                if(e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }

    private fun File.toBodyPart(): MultipartBody.Part{
        val reqFile = RequestBody.create(MediaType.parse("image/*"), this)
        return MultipartBody.Part.createFormData("avatar", this.name, reqFile)
    }

    private fun EditProfileRequest.toRequestBody() = RequestBody.create(MediaType.parse("application/json"), Gson().toJson(this))


}