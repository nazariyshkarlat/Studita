package com.example.studita.presentation.fragments.friends

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.example.studita.utils.PrefsUtils

class MyFriendsFragment : FriendsFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments = bundleOf(
            "USER_ID" to PrefsUtils.getUserId(),
            "GLOBAL_SEARCH_ONLY" to (arguments?.getBoolean("GLOBAL_SEARCH_ONLY") == true)
        )
        super.onViewCreated(view, savedInstanceState)
    }

}