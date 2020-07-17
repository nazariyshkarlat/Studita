package com.example.studita.presentation.view_model

import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.EditProfileModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.EditProfileData
import com.example.studita.domain.entity.EditProfileRequestData
import com.example.studita.domain.interactor.EditProfileStatus
import com.example.studita.domain.interactor.UserNameAvailableStatus
import com.example.studita.utils.UserUtils
import com.example.studita.utils.asyncExt
import com.example.studita.utils.launchExt
import kotlinx.android.synthetic.main.edit_profile_layout.*
import kotlinx.coroutines.*
import org.w3c.dom.Text

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
    var countersErrorState = MutableLiveData<Pair<TextField, Boolean>>()
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

    fun checkUserNameAvailable(){
        job = viewModelScope.launchExt(job) {
            when(editProfileInteractor.isUserNameAvailable(newProfileData.userName!!)){
                is UserNameAvailableStatus.Available -> userNameAvailableState.postValue(UserNameAvailable.AVAILABLE)
                is UserNameAvailableStatus.Unavailable -> userNameAvailableState.postValue(UserNameAvailable.IS_TAKEN)
            }
        }
    }

    fun checkCorrectUserName(){
        if(editProfileInteractor.isProfileDataChanged(oldProfileData, newProfileData, avaChanged)) {
            if (editProfileInteractor.isValidUserFullNameLength(newProfileData) && (userNameAvailableState.value == UserNameAvailable.AVAILABLE))
                saveChangesButtonVisibleState.value = true
        }
    }

    fun backClick(){
        backClickState.value = if(editProfileInteractor.isProfileDataChanged(oldProfileData, newProfileData, avaChanged)) BackClickState.SHOW_DIALOG else BackClickState.CLOSE
    }

    fun verifyUserName(){
        if(editProfileInteractor.isUserNameChanged(oldProfileData, newProfileData)) {
                if(editProfileInteractor.isValidUserNameLength(newProfileData)) {
                    countersErrorState.value = TextField.USER_NAME to false
                    checkUserNameAvailable()
                }else {
                    userNameAvailableState.value = UserNameAvailable.INVALID_LENGTH
                    countersErrorState.value = TextField.USER_NAME to true
                }
        }else{
            countersErrorState.value = TextField.USER_NAME to false
            userNameAvailableState.value = UserNameAvailable.AVAILABLE
        }
    }

    fun checkIfUserNameAvailableAndCorrectFullName(){
        if(editProfileInteractor.isValidUserFullNameLength(newProfileData)) {
            countersErrorState.value = TextField.USER_FULL_NAME to false
            if (userNameAvailable() && editProfileInteractor.isProfileDataChanged(oldProfileData, newProfileData, avaChanged)) {
                saveChangesButtonVisibleState.value = true
            }
        }else
            countersErrorState.value = TextField.USER_FULL_NAME to true
    }

    fun checkShowSaveButton(){
        saveChangesButtonVisibleState.value = editProfileInteractor.isValidData(oldProfileData, newProfileData, avaChanged) && userNameAvailable()
    }

    private fun userNameAvailable() = userNameAvailableState.value == UserNameAvailable.AVAILABLE

    fun formNewUserName(charSequence: CharSequence?){
        if(charSequence.toString().length >= 2)
            newProfileData.userName = charSequence?.toString()?.substring(1)?.takeIf { it.isNotEmpty() }
    }

    fun formNewUserFullName(charSequence: CharSequence?){
        newProfileData.userFullName = charSequence?.toString()?.takeIf { it.isNotEmpty() }
    }

    private fun applyLocalChanges(avatarLink: String?){
        UserUtils.userData.userName = newProfileData.userName
        UserUtils.userData.userFullName = newProfileData.userFullName
        UserUtils.userData.avatarLink = avatarLink
        UserUtils.userDataLiveData.postValue(UserUtils.userData)
        GlobalScope.launch {
            userDataInteractor.saveUserData(UserUtils.userData)
        }
    }

    enum class TextField{
        USER_NAME,
        USER_FULL_NAME
    }

    enum class UserNameAvailable{
        AVAILABLE,
        INVALID_LENGTH,
        IS_TAKEN
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