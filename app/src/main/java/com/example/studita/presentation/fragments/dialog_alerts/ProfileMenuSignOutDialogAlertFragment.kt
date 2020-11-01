package com.example.studita.presentation.fragments.dialog_alerts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.App
import com.example.studita.R
import com.example.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.example.studita.presentation.activities.MainActivity
import com.example.studita.presentation.activities.MainActivity.Companion.startMainActivityNewTask
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import com.example.studita.presentation.view_model.ProfileMenuFragmentViewModel
import com.example.studita.utils.UserUtils
import com.example.studita.utils.UserUtils.deviceSignOut
import kotlinx.android.synthetic.main.dialog_alert_layout.*
import kotlinx.android.synthetic.main.sign_out_dialog_alert.*

class ProfileMenuSignOutDialogAlertFragment : BaseDialogFragment(R.layout.dialog_alert_layout) {

    private var profileMenuFragmentViewModel: ProfileMenuFragmentViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setText(resources.getString(R.string.sign_out_dialog_alert_title),
            resources.getString(R.string.sign_out_dialog_alert_subtitle),
            resources.getString(R.string.cancel),
            resources.getString(R.string.sign_out)
        )
        profileMenuFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(ProfileMenuFragmentViewModel::class.java)
        }

        dialogAlertLeftButton.setOnClickListener { dismiss() }
        dialogAlertRightButton.setOnClickListener {
            activity?.application?.let {

                profileMenuFragmentViewModel?.signOut(
                    UserUtils.getUserIDTokenData()!!,
                    it
                )

                deviceSignOut()
                dialog?.dismiss()
                App.authenticate(null, false)
                activity?.startMainActivityNewTask()
            }
        }
    }

}