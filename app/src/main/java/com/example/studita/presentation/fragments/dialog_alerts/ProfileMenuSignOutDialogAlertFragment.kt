package com.example.studita.presentation.fragments.dialog_alerts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.App
import com.example.studita.R
import com.example.studita.presentation.activities.MainActivity
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import com.example.studita.presentation.view_model.ProfileMenuFragmentViewModel
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import kotlinx.android.synthetic.main.sign_out_dialog_alert.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProfileMenuSignOutDialogAlertFragment : BaseDialogFragment(R.layout.sign_out_dialog_alert){

    private var profileMenuFragmentViewModel: ProfileMenuFragmentViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileMenuFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(ProfileMenuFragmentViewModel::class.java)
        }

        signOutDialogLeftButton.setOnClickListener { dismiss() }
        signOutDialogRightButton.setOnClickListener {
            activity?.application?.let {

                profileMenuFragmentViewModel?.signOut(
                    UserUtils.getUserIDTokenData()!!,
                    it
                )

                runBlocking {
                    profileMenuFragmentViewModel?.deleteUserData()
                    deviceSignOut()
                }
            }
        }
    }

    private fun deviceSignOut(){
        UserUtils.userDataLiveData.removeObservers(activity as FragmentActivity)
        UserUtils.clearUserIdToken()
        UserUtils.userDataLiveData.value = null
        MainActivity.needsRecreate = true
        App.getUserData()
        dialog?.dismiss()
        activity?.finish()
    }

}