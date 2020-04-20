package com.example.studita.presentation.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.example.studita.R
import com.example.studita.di.DiskModule
import com.example.studita.presentation.activities.DefaultActivity
import com.example.studita.presentation.activities.DefaultActivity.Companion.themeState
import com.example.studita.presentation.activities.MainActivity
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import com.example.studita.presentation.utils.makeView
import kotlinx.android.synthetic.main.dialog_list_layout.*
import kotlinx.android.synthetic.main.dialog_list_layout_radio_button_item.view.*

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