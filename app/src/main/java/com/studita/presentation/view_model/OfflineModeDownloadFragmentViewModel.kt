package com.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.App
import com.studita.App.Companion.authenticate
import com.studita.di.data.OfflineDataModule
import com.studita.domain.interactor.*
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import com.studita.utils.launchExt
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OfflineModeDownloadFragmentViewModel : ViewModel(){

    val downloadCacheEvent = SingleLiveEvent<Boolean>()
    val downloadProgressLiveData = MutableLiveData<Triple<Float, Float, Boolean>>()

    val errorState = MutableLiveData<Boolean>()

    val interactor = OfflineDataModule.getOfflineDataInteractorImpl(downloadProgressLiveData)

    var job : Job? = null

    init {
        downloadCache()
    }

    fun downloadCache() {
        job = viewModelScope.launchExt(job) {
            when(interactor.downloadOfflineData()){
                is DownloadOfflineDataStatus.Success -> {
                    PrefsUtils.setOfflineDataIsCached()
                    downloadCacheEvent.value = true
                    authenticate(UserUtils.getUserIDTokenData(), false)
                }
                is DownloadOfflineDataStatus.NoConnection -> {
                    errorState.value = true
                }
                else -> {
                    errorState.value = false
                    delay(1000)
                    downloadCache()
                }
            }
        }
    }
}