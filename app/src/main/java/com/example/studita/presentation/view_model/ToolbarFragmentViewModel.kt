package com.example.studita.presentation.view_model

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studita.presentation.fragments.base.NavigatableFragment

class ToolbarFragmentViewModel : ViewModel(){

    val toolbarTextState = MutableLiveData<String?>()
    val toolbarDividerState = SingleLiveEvent<Boolean>()
    val progressState = MutableLiveData<Boolean>()
    val toolbarRightButtonState = MutableLiveData<Pair<Int, (View) -> Unit>>()
    val toolbarFragmentOnNavigateState = MutableLiveData<NavigatableFragment.OnNavigateFragment>()

    fun setToolbarText(text: String?){
        println(text)
        toolbarTextState.value = text
    }

    fun showToolbarDivider(show: Boolean){
        toolbarDividerState.value = show
    }

    fun showRightButtonAndSetOnClick(@DrawableRes iconRes: Int, onClick:(View) -> Unit){
        toolbarRightButtonState.value = iconRes to onClick
    }

    fun hideRightButton(){
        toolbarRightButtonState.value = null
    }

    fun hideProgress() {
        progressState.value = false
    }

    fun showProgress() {
        progressState.value = true
    }

    fun hideDivider() {
        toolbarDividerState.value = false
    }

}