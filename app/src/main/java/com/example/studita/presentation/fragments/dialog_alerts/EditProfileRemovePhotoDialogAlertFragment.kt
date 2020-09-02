package com.example.studita.presentation.fragments.dialog_alerts

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_alert_layout.*

class EditProfileRemovePhotoDialogAlertFragment :
    BaseDialogFragment(R.layout.dialog_alert_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setText(resources.getString(R.string.edit_profile_remove_photo_dialog_alert_title),
            resources.getString(R.string.edit_profile_remove_photo_dialog_alert_subtitle),
            resources.getString(R.string.cancel),
            resources.getString(R.string.delete)
        )
        dialogAlertLeftButton.setOnClickListener { dismiss() }
        dialogAlertRightButton.setOnClickListener {
            dismiss()
            if (targetFragment != null)
                targetFragment?.onActivityResult(
                    345,
                    Activity.RESULT_OK,
                    Intent().putExtra("SELECTED_IMAGE", null as Bitmap?)
                )
        }
    }

}