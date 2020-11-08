package com.studita.presentation.fragments.dialog_alerts

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.activities.DefaultActivity
import com.studita.presentation.activities.MainActivity
import com.studita.presentation.view_model.MainMenuActivityViewModel
import com.studita.utils.PrefsUtils
import com.studita.utils.ThemeUtils
import kotlinx.android.synthetic.main.dialog_list_layout.*

class MainMenuThemeDialogAlertFragment : RadioButtonListDialogAlertFragment() {

    private var mainMenuActivityViewModel: MainMenuActivityViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        items = listOf(
            resources.getString(R.string.dark_theme),
            resources.getString(R.string.light_theme)
        )
        selectedPos = PrefsUtils.getTheme().ordinal

        super.onViewCreated(view, savedInstanceState)

        mainMenuActivityViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainMenuActivityViewModel::class.java)
        }

        dialogListLayoutTitle.text = resources.getString(R.string.app_theme)

        dialogListLayoutLeftButton.setOnClickListener { dialog?.dismiss() }
        dialogListLayouRightButton.setOnClickListener {
            dialog?.dismiss()
            changeTheme(selectedPos)
        }
    }

    private fun changeTheme(position: Int) {
        mainMenuActivityViewModel?.onThemeChangeListener?.onThemeChanged(ThemeUtils.Theme.values()[position])
        MainActivity.needsRefresh = true
    }

    interface OnThemeChangeListener {
        fun onThemeChanged(theme: ThemeUtils.Theme)
    }
}