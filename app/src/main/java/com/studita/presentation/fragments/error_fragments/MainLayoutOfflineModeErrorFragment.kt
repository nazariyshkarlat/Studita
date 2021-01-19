package com.studita.presentation.fragments.error_fragments

import android.os.Bundle
import android.view.View
import com.studita.App
import com.studita.R
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.listeners.ReloadPageCallback
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import com.studita.utils.removeFragment
import kotlinx.android.synthetic.main.main_layout_offline_mode_error.*

class MainLayoutOfflineModeErrorFragment : BaseFragment(R.layout.main_layout_offline_mode_error) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainLayoutOfflineModeErrorDisableOfflineModeButton.setOnClickListener {
            PrefsUtils.setOfflineMode(false)
            App.authenticate(UserUtils.getUserIDTokenData(), true)
            fragmentManager!!.removeFragment(this)
            mainLayoutOfflineModeErrorDisableOfflineModeButton.isClickable = false
            mainLayoutOfflineModeErrorDisableOfflineModeButton.isEnabled = false
        }
    }

}