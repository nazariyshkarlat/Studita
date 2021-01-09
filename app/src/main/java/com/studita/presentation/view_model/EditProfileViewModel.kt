package com.studita.presentation.view_model

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.App
import com.studita.domain.entity.EditProfileData
import com.studita.domain.entity.EditProfileRequestData
import com.studita.domain.interactor.*
import com.studita.domain.interactor.edit_profile.EditProfileInteractor
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import com.studita.utils.launchExt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class EditProfileViewModel() : ViewModel() {

    private val editProfileInteractor = GlobalContext.get().get<EditProfileInteractor>()
    private val userDataInteractor = GlobalContext.get().get<UserDataInteractor>()
    private var job: Job? = null

    var oldProfileData: EditProfileData? = null
    var newProfileData: EditProfileData? = null
    var selectedImage: Bitmap? = null

    var avaChanged = false

    val errorEvent = SingleLiveEvent<Boolean>()
    val progressState = MutableLiveData(true)

    var userNameAvailableState = MutableLiveData<UserNameAvailable>()
    var saveChangesButtonVisibleState = MutableLiveData<Boolean>(false)
    var countersErrorState = MutableLiveData<Boolean>()
    var backClickState = SingleLiveEvent<BackClickState>()
    var saveProfileChangesState = MutableLiveData<SaveProfileChangesState>()

    init {
        getUserData()
    }

    fun getUserData(){
        progressState.value = true
        viewModelScope.launch {

            if(App.userDataDeferred.isCompleted && App.userDataDeferred.await() !is UserDataStatus.Success)
                App.authenticate(UserUtils.getUserIDTokenData(), true)

            when(val userData = if(!App.userDataDeferred.isCompleted) App.userDataDeferred.await() else userDataInteractor.getUserData(PrefsUtils.getUserId(), false, true)){
                is UserDataStatus.NoConnection -> errorEvent.value = true
                is UserDataStatus.ServiceUnavailable -> errorEvent.value = false
                is UserDataStatus.Success -> {

                    when(App.userDataDeferred.await()) {
                        is UserDataStatus.Success -> {
                            UserUtils.userDataLiveData.value = userData.result
                            progressState.value = false
                        }
                        is UserDataStatus.NoConnection -> {
                            errorEvent.value = true
                        }
                        else -> {
                            errorEvent.value = false
                        }
                    }
                }
            }
        }
    }

    fun saveProfileChanges() {
        saveProfileChangesState.value = SaveProfileChangesState.LOADING
        job = viewModelScope.launchExt(job) {
            when (val result = editProfileInteractor.editProfile(
                EditProfileRequestData(
                    UserUtils.getUserIDTokenData()!!,
                    newProfileData!!
                ), UserUtils.userData, selectedImage
            )) {
                is EditProfileStatus.Success -> {
                    applyLocalChanges(result.avatarLink)
                    saveProfileChangesState.value = SaveProfileChangesState.SUCCESS
                }
                is EditProfileStatus.NoConnection -> {
                    saveProfileChangesState.value =
                        SaveProfileChangesState.FAILURE
                    userNameAvailableState.value = UserNameAvailable.Unavailable(ErrorType.CONNECTION_ERROR)
                }
                is EditProfileStatus.ServiceUnavailable -> {
                    saveProfileChangesState.value =
                        SaveProfileChangesState.FAILURE
                    userNameAvailableState.value = UserNameAvailable.Unavailable(ErrorType.SERVER_ERROR)
                }
                is EditProfileStatus.Failure -> {
                    saveProfileChangesState.value =
                        SaveProfileChangesState.FAILURE
                    userNameAvailableState.value = UserNameAvailable.Unavailable(ErrorType.SERVER_ERROR)
                }
            }
        }
    }

    private fun checkUserNameAvailable() {
        job = viewModelScope.launchExt(job) {
            when (editProfileInteractor.isUserNameAvailable(newProfileData!!.userName!!)) {
                is UserNameAvailableStatus.Available -> userNameAvailableState.value =
                    UserNameAvailable.Available

                is UserNameAvailableStatus.IsTaken -> userNameAvailableState.value =
                    UserNameAvailable.Unavailable(ErrorType.IS_TAKEN)
                is UserNameAvailableStatus.Unavailable -> userNameAvailableState.value =
                    UserNameAvailable.Unavailable(ErrorType.UNAVAILABLE)
                is UserNameAvailableStatus.ServiceUnavailable -> userNameAvailableState.value =
                    UserNameAvailable.Unavailable(ErrorType.SERVER_ERROR)
                is UserNameAvailableStatus.Failure -> userNameAvailableState.value =
                    UserNameAvailable.Unavailable(ErrorType.SERVER_ERROR)
                is UserNameAvailableStatus.NoConnection -> userNameAvailableState.value =
                    UserNameAvailable.Unavailable(ErrorType.CONNECTION_ERROR)
            }
        }
    }

    fun checkCorrectUserName() {
        if (editProfileInteractor.isProfileDataChanged(
                oldProfileData!!,
                newProfileData!!,
                avaChanged
            )
        ) {
            if (userNameAvailableState.value == UserNameAvailable.Available)
                saveChangesButtonVisibleState.value = true
        }
    }

    fun backClick() {
        backClickState.value = if ((progressState.value == false) && editProfileInteractor.isProfileDataChanged(
                oldProfileData!!,
                newProfileData!!,
                avaChanged
            )) BackClickState.SHOW_DIALOG else BackClickState.CLOSE
    }

    fun verifyUserName() {
        if (editProfileInteractor.isUserNameChanged(oldProfileData!!, newProfileData!!)) {
            if (editProfileInteractor.isValidUserNameLength(newProfileData!!)) {
                countersErrorState.value = false
                checkUserNameAvailable()
            } else {
                userNameAvailableState.value = UserNameAvailable.Unavailable(ErrorType.UNAVAILABLE)
                countersErrorState.value = true
                job?.cancel()
            }
        } else {
            countersErrorState.value = false
            userNameAvailableState.value = UserNameAvailable.Available
        }
    }


    fun checkShowSaveButton() {
        println(userNameAvailableState.value)
        saveChangesButtonVisibleState.value = editProfileInteractor.isValidData(
            oldProfileData!!,
            newProfileData!!,
            avaChanged
        ) && userNameAvailable()
    }

    private fun userNameAvailable() = userNameAvailableState.value == UserNameAvailable.Available

    fun formNewUserName(charSequence: CharSequence?) {
        newProfileData!!.userName = charSequence?.substring(1, charSequence.length)?.takeIf { it.isNotEmpty() }
    }

    fun formNewName(charSequence: CharSequence?) {
        newProfileData!!.name = charSequence?.toString()?.takeIf { it.isNotEmpty() }
    }

    fun formNewBio(charSequence: CharSequence?) {
        newProfileData!!.bio = charSequence?.toString()?.takeIf { it.isNotEmpty() }
    }

    private fun applyLocalChanges(avatarLink: String?) {
        UserUtils.userData.userName = newProfileData!!.userName
        UserUtils.userData.name = newProfileData!!.name
        UserUtils.userData.bio = newProfileData!!.bio
        UserUtils.userData.avatarLink = avatarLink
        UserUtils.userDataLiveData.value = UserUtils.userData
        GlobalScope.launch(Dispatchers.Main){
            userDataInteractor.saveUserData(UserUtils.userData)
        }
    }

    sealed class UserNameAvailable {
        object Available : UserNameAvailable()
        class Unavailable(val errorType: ErrorType) : UserNameAvailable()
    }

    enum class ErrorType{
        SERVER_ERROR,
        CONNECTION_ERROR,
        UNAVAILABLE,
        IS_TAKEN
    }


    enum class BackClickState {
        SHOW_DIALOG,
        CLOSE
    }

    enum class SaveProfileChangesState {
        LOADING,
        FAILURE,
        SUCCESS
    }

}