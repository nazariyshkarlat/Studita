package com.example.studita.presentation.fragments.dialog_alerts

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import com.example.studita.presentation.view_model.EditProfileViewModel
import com.example.studita.utils.UserUtils
import kotlinx.android.synthetic.main.edit_profile_remove_photo_dialog_alert.*
import kotlinx.android.synthetic.main.unsaved_changes_dialog_alert.*

class EditProfileRemovePhotoDialogAlertFragment : BaseDialogFragment(R.layout.edit_profile_remove_photo_dialog_alert){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editProfileRemovePhotoDialogLeftButton.setOnClickListener { dismiss() }
        editProfileRemovePhotoDialogRightButton.setOnClickListener {
            dismiss()
            if(targetFragment != null)
                targetFragment?.onActivityResult(345, Activity.RESULT_OK, Intent().putExtra("SELECTED_IMAGE", null as Bitmap?))
        }
    }

}