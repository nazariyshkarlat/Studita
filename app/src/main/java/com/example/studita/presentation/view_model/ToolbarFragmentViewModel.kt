package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ToolbarFragmentViewModel : ViewModel(){

    val toolbarTextState = MutableLiveData<String?>()
    val toolbarDividerState = SingleLiveEvent<Boolean>()

    fun setToolbarText(text: String?){
        toolbarTextState.value = text
    }

    fun showToolbarDivider(show: Boolean){
        toolbarDividerState.value = show
    }

}