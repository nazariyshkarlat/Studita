package com.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import com.studita.presentation.fragments.dialog_alerts.MainMenuThemeDialogAlertFragment

class MainMenuActivityViewModel : ViewModel() {

    var onThemeChangeListener: MainMenuThemeDialogAlertFragment.OnThemeChangeListener? = null

}