package com.studita.presentation.fragments.profile

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.studita.utils.PrefsUtils

class MyProfileFragment : ProfileFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments = bundleOf("USER_ID" to PrefsUtils.getUserId())
        super.onViewCreated(view, savedInstanceState)
    }

}