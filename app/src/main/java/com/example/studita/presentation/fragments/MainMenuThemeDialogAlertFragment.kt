package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import com.example.studita.R
import com.example.studita.presentation.activities.DefaultActivity
import com.example.studita.presentation.activities.DefaultActivity.Companion.themeState
import com.example.studita.presentation.activities.MainActivity
import kotlinx.android.synthetic.main.dialog_list_layout.*

class MainMenuThemeDialogAlertFragment : RadioButtonListDialogAlertFragment(){

    lateinit var onThemeChangeListener: OnThemeChangeListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        items = listOf(resources.getString(R.string.dark_theme), resources.getString(R.string.light_theme))
        selectedPos = themeState.ordinal

        super.onViewCreated(view, savedInstanceState)

        dialogListLayoutTitle.text = resources.getString(R.string.app_theme)

        dialogListLayoutLeftButton.setOnClickListener { dialog?.hide() }
        dialogListLayouRightButton.setOnClickListener {
            changeTheme(selectedPos)
        }
    }

    private fun changeTheme(position: Int){
        onThemeChangeListener.onThemeChanged(DefaultActivity.Theme.values()[position])
        MainActivity.needsRecreate = true
    }

    interface OnThemeChangeListener{
        fun onThemeChanged(theme: DefaultActivity.Theme)
    }
}