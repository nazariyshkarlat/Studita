package com.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import com.studita.presentation.fragments.main.MainMenuFragment

class MainMenuActivityViewModel : ViewModel() {

    var onThemeChangeListener: MainMenuFragment.OnThemeChangeListener? = null

}