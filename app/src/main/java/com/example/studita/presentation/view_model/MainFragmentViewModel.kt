package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainFragmentViewModel : ViewModel(){

    val fabState = MutableLiveData<Boolean>()
    val progressState = MutableLiveData<Boolean>(true)

    fun showFab(show: Boolean){
        fabState.value = show
    }

    fun showProgress(show: Boolean){
        progressState .value = show
    }

}