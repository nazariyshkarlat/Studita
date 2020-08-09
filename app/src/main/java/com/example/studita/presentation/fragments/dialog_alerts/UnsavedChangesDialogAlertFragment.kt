package com.example.studita.presentation.fragments.dialog_alerts

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.View
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import kotlinx.android.synthetic.main.unsaved_changes_dialog_alert.*

class UnsavedChangesDialogAlertFragment :
    BaseDialogFragment(R.layout.unsaved_changes_dialog_alert) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        unsavedChangesDialogLeftButton.setOnClickListener { dismiss() }
        unsavedChangesDialogRightButton.setOnClickListener {
            dismiss()
            if (targetFragment != null)
                targetFragment?.onActivityResult(228, RESULT_OK, null)
        }
    }

}