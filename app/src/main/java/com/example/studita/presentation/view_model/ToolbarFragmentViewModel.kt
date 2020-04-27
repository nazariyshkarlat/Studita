package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studita.presentation.fragments.base.NavigatableFragment

class ToolbarFragmentViewModel : ViewModel(){

    val toolbarTextState = MutableLiveData<String?>()
    val toolbarDividerState = SingleLiveEvent<Boolean>()
    val toolbarFragmentOnNavigateState = MutableLiveData<NavigatableFragment.OnNavigateFragment>()

    fun setToolbarText(text: String?){
        toolbarTextState.value = text
    }

    fun showToolbarDivider(show: Boolean){
        toolbarDividerState.value = show
    }

}