package com.studita.presentation.fragments.dialog_alerts

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.View
import com.studita.R
import com.studita.presentation.fragments.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_alert_layout.*

class UnsavedChangesDialogAlertFragment :
    BaseDialogFragment(R.layout.dialog_alert_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setText(resources.getString(R.string.unsaved_changes_dialog_alter_title),
            resources.getString(R.string.unsaved_changes_dialog_alter_subtitle),
            resources.getString(R.string.cancel),
            resources.getString(R.string.leave)
        )
        dialogAlertLeftButton.setOnClickListener { dismiss() }
        dialogAlertRightButton.setOnClickListener {
            dismiss()
            if (targetFragment != null)
                targetFragment?.onActivityResult(228, RESULT_OK, null)
        }
    }

}