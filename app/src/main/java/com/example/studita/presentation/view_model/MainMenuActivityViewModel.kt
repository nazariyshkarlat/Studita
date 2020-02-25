package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainMenuActivityViewModel : ViewModel(){

    var toolbarHeight = 0

    val toolbarTextState = SingleLiveEvent<String>()

    fun setToolbarText(text: String){
        toolbarTextState.value = text
    }

}