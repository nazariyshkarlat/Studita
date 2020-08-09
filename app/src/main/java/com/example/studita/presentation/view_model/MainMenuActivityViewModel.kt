package com.example.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import com.example.studita.presentation.fragments.dialog_alerts.MainMenuThemeDialogAlertFragment

class MainMenuActivityViewModel : ViewModel() {

    var onThemeChangeListener: MainMenuThemeDialogAlertFragment.OnThemeChangeListener? = null

}