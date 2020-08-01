package com.example.studita.presentation.view_model

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.di.data.EditProfileModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.EditProfileData
import com.example.studita.domain.entity.EditProfileRequestData
import com.example.studita.domain.interactor.EditProfileStatus
import com.example.studita.domain.interactor.UserNameAvailableStatus
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.*

class EditProfileViewModel : ViewModel(){

    private val editProfileInteractor = EditProfileModule.getEditProfileInteractorImpl()
    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()
    private var job: Job? = null

    lateinit var oldProfileData: EditProfileData
    lateinit var newProfileData: EditProfileData
    var selectedImage: Bitmap? = null

    var avaChanged = false

    var userNameAvailableState =  MutableLiveData<UserNameAvailable>()
    var saveChangesButtonVisibleState = MutableLiveData<Boolean>(false)
    var countersErrorState = MutableLiveData<Boolean>()
    var backClickState = SingleLiveEvent<BackClickState>()
    var saveProfileChangesState = MutableLiveData<SaveProfileChangesState>()

    fun saveProfileChanges(){
        saveProfileChangesState.value = SaveProfileChangesState.LOADING
        job = viewModelScope.launchExt(job) {
            when(val result = editProfileInteractor.editProfile(EditProfileRequestData(UserUtils.getUserIDTokenData()!!, newProfileData), UserUtils.userData, selectedImage)){
                is EditProfileStatus.Success -> {
                    applyLocalChanges(result.avatarLink)
                    saveProfileChangesState.postValue(SaveProfileChangesState.SUCCESS)
                }
                EditProfileStatus.Failure -> saveProfileChangesState.postValue(SaveProfileChangesState.FAILURE)
            }
        }
    }

    private fun checkUserNameAvailable(){
        job = viewModelScope.launchExt(job) {
            when(editProfileInteractor.isUserNameAvailable(newProfileData.userName!!)){
                is UserNameAvailableStatus.Available -> userNameAvailableState.postValue(UserNameAvailable.AVAILABLE)
                is UserNameAvailableStatus.IsTaken -> userNameAvailableState.postValue(UserNameAvailable.IS_TAKEN)
                is UserNameAvailableStatus.Failure -> userNameAvailableState.postValue(UserNameAvailable.UNAVAILABLE)
            }
        }
    }

    fun checkCorrectUserName(){
        if(editProfileInteractor.isProfileDataChanged(oldProfileData, newProfileData, avaChanged)) {
            if (userNameAvailableState.value == UserNameAvailable.AVAILABLE)
                saveChangesButtonVisibleState.value = true
        }
    }

    fun backClick(){
        backClickState.value = if(editProfileInteractor.isProfileDataChanged(oldProfileData, newProfileData, avaChanged)) BackClickState.SHOW_DIALOG else BackClickState.CLOSE
    }

    fun verifyUserName(){
        if(editProfileInteractor.isUserNameChanged(oldProfileData, newProfileData)) {
                if(editProfileInteractor.isValidUserNameLength(newProfileData)) {
                    countersErrorState.value = false
                    checkUserNameAvailable()
                }else {
                    userNameAvailableState.value = UserNameAvailable.UNAVAILABLE
                    countersErrorState.value = true
                    job?.cancel()
                }
        }else{
            countersErrorState.value = false
            userNameAvailableState.value = UserNameAvailable.AVAILABLE
        }
    }


    fun checkShowSaveButton(){
        saveChangesButtonVisibleState.value = editProfileInteractor.isValidData(oldProfileData, newProfileData, avaChanged) && userNameAvailable()
    }

    private fun userNameAvailable() = userNameAvailableState.value == UserNameAvailable.AVAILABLE

    fun formNewUserName(charSequence: CharSequence?){
        newProfileData.userName = charSequence?.toString()?.takeIf { it.isNotEmpty() }
    }

    fun formNewName(charSequence: CharSequence?){
        newProfileData.name = charSequence?.toString()?.takeIf { it.isNotEmpty() }
    }

    private fun applyLocalChanges(avatarLink: String?){
        UserUtils.userData.userName = newProfileData.userName
        UserUtils.userData.name = newProfileData.name
        UserUtils.userData.avatarLink = avatarLink
        UserUtils.userDataLiveData.postValue(UserUtils.userData)
        GlobalScope.launch {
            userDataInteractor.saveUserData(UserUtils.userData)
        }
    }

    enum class UserNameAvailable{
        AVAILABLE,
        IS_TAKEN,
        UNAVAILABLE
    }


    enum class BackClickState{
        SHOW_DIALOG,
        CLOSE
    }

    enum class SaveProfileChangesState{
        LOADING,
        FAILURE,
        SUCCESS
    }

}