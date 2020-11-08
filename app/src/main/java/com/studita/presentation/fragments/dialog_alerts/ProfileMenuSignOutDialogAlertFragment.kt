package com.studita.presentation.fragments.dialog_alerts

import android.os.Bundle
import android.view.View
import com.studita.R
import com.studita.authenticator.AccountAuthenticator
import com.studita.presentation.activities.MainActivity.Companion.startMainActivityNewTask
import com.studita.presentation.fragments.base.BaseDialogFragment
import com.studita.utils.UserUtils
import kotlinx.android.synthetic.main.dialog_alert_layout.*

class ProfileMenuSignOutDialogAlertFragment : BaseDialogFragment(R.layout.dialog_alert_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setText(resources.getString(R.string.sign_out_dialog_alert_title),
            resources.getString(R.string.sign_out_dialog_alert_subtitle),
            resources.getString(R.string.cancel),
            resources.getString(R.string.sign_out)
        )

        dialogAlertLeftButton.setOnClickListener { dismiss() }
        dialogAlertRightButton.setOnClickListener {
            activity?.application?.let {
                UserUtils.signOut(it)
                AccountAuthenticator.removeAccount(it)
            }
            dialog?.dismiss()
            activity?.startMainActivityNewTask()
        }
    }

}