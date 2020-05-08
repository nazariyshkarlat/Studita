package com.example.studita.presentation.fragments.dialog_alerts

import android.os.Bundle
import android.view.View
import com.example.studita.R
import kotlinx.android.synthetic.main.dialog_list_layout.*

class MainMenuLanguageDialogAlertFragment : RadioButtonListDialogAlertFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        items = listOf(
            resources.getString(R.string.russian),
            resources.getString(R.string.english)
        )
        selectedPos = 0

        super.onViewCreated(view, savedInstanceState)

        dialogListLayoutTitle.text = resources.getString(R.string.language)

        dialogListLayoutLeftButton.setOnClickListener { dialog?.dismiss() }
        dialogListLayouRightButton.setOnClickListener {
            dialog?.dismiss()
        }
    }
}