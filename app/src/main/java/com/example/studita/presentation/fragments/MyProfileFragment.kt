package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.example.studita.utils.UserUtils

class MyProfileFragment : ProfileFragment(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments = bundleOf("USER_ID" to UserUtils.userData.userId!!)
        super.onViewCreated(view, savedInstanceState)
    }

}