package com.studita.presentation.fragments.error_fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.studita.App
import com.studita.R
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.fragments.main.HomeFragment
import com.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.studita.presentation.listeners.ReloadPageCallback
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.*
import kotlinx.android.synthetic.main.internet_is_disabled_layout.*
import kotlinx.android.synthetic.main.internet_is_disabled_main_layout.*
import kotlinx.android.synthetic.main.server_problems_main_layout.*
import kotlinx.coroutines.*

class InternetIsDisabledMainFragment : BaseFragment(R.layout.internet_is_disabled_main_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState == null) {
            (parentFragment as HomeFragment).homeFragmentViewModel?.getOfflineLevels()
        }

        internetIsDisabledMainLayoutTryAgainButton.setOnSingleClickListener {
            internetIsDisabledMainLayoutEnableOfflineModeButton.setOnClickListener {  }
            internetIsDisabledMainLayoutTryAgainButton.animateRefreshButton()
            (parentFragment as HomeFragment).homeFragmentViewModel?.progressState?.value = true
            lifecycleScope.launch(Dispatchers.Main) {
                delay(400)
                (parentFragment as? ReloadPageCallback)?.onPageReload()
                fragmentManager!!.removeFragment(this@InternetIsDisabledMainFragment)
            }
        }

        internetIsDisabledMainLayoutEnableOfflineModeButton.setOnClickListener {
            internetIsDisabledMainLayoutTryAgainButton.setOnClickListener {  }
            PrefsUtils.setOfflineMode(true)
            (parentFragment as? ReloadPageCallback)?.onPageReload()
            removeFragmentWithAnimation(view)
            (parentFragment as? HomeFragment)?.showEnableOfflineModeSnackbar()
        }

        view.setOnTouchListener { _, _ ->  true}
    }

}