package com.studita.presentation.fragments.dialog_alerts

import android.os.Bundle
import android.view.View
import androidx.core.view.get
import com.studita.R
import com.studita.utils.disableAllItems
import kotlinx.android.synthetic.main.dialog_list_layout.*

class MainMenuLanguageDialogAlertFragment : RadioButtonListDialogAlertFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        items = listOf(
            resources.getString(R.string.russian),
            resources.getString(R.string.english)
        )
        selectedPos = 0

        super.onViewCreated(view, savedInstanceState)

        dialogListLayoutListLayout.getChildAt(1).disableAllItems()

        dialogListLayoutTitle.text = resources.getString(R.string.language)

        dialogListLayoutLeftButton.setOnClickListener { dismiss() }
        dialogListLayouRightButton.setOnClickListener {
            dismiss()
        }
    }
}