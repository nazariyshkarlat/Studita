package com.example.studita.presentation.view_model

import android.view.View
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studita.presentation.fragments.base.NavigatableFragment

class ToolbarFragmentViewModel : ViewModel() {

    val toolbarTextState = MutableLiveData<String?>()
    val toolbarDividerState = SingleLiveEvent<Boolean>()
    val progressState = MutableLiveData<Boolean>()
    val toolbarRightButtonState = MutableLiveData<ToolbarRightButtonState>()
    val toolbarFragmentOnNavigateState = MutableLiveData<NavigatableFragment.OnNavigateFragment>()

    fun setToolbarText(text: String?) {
        toolbarTextState.value = text
    }

    fun showToolbarDivider(show: Boolean) {
        toolbarDividerState.value = show
    }

    fun setToolbarRightButtonState(toolbarRightButtonState: ToolbarRightButtonState) {
        this.toolbarRightButtonState.value = toolbarRightButtonState
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

    sealed class ToolbarRightButtonState {
        class IsEnabled(@DrawableRes val imageRes: Int, val onClick: (View) -> Unit) :
            ToolbarRightButtonState()

        class Disabled(@DrawableRes val imageRes: Int) : ToolbarRightButtonState()
        object Invisible : ToolbarRightButtonState()
    }

}