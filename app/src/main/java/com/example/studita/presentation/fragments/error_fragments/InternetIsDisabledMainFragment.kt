package com.example.studita.presentation.fragments.error_fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.studita.App
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.fragments.main.HomeFragment
import com.example.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.example.studita.presentation.listeners.ReloadPageCallback
import com.example.studita.presentation.views.CustomSnackbar
import com.example.studita.utils.*
import kotlinx.android.synthetic.main.internet_is_disabled_layout.*
import kotlinx.android.synthetic.main.internet_is_disabled_main_layout.*
import kotlinx.coroutines.*

class InternetIsDisabledMainFragment : BaseFragment(R.layout.internet_is_disabled_main_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState == null) {
            (parentFragment as HomeFragment).homeFragmentViewModel?.getOfflineLevels()
        }

        internetIsDisabledMainLayoutTryAgainButton.setOnSingleClickListener {
            internetIsDisabledMainLayoutTryAgainButton.animateRefreshButton()
            (parentFragment as HomeFragment).homeFragmentViewModel?.progressState?.value = false
            lifecycleScope.launch(Dispatchers.Main) {
                delay(400)
                (parentFragment as? ReloadPageCallback)?.onPageReload()
                fragmentManager!!.removeFragment(this@InternetIsDisabledMainFragment)
            }
        }

        internetIsDisabledMainLayoutEnableOfflineModeButton.setOnClickListener {
            PrefsUtils.setOfflineMode(true)
            (parentFragment as? ReloadPageCallback)?.onPageReload()
            removeFragmentWithAnimation(view)

            CustomSnackbar(context!!).show(
                resources.getString(R.string.enable_offline_mode_snackbar), ThemeUtils.getAccentColor(context!!)
            )
        }

        view.setOnTouchListener { _, _ ->  true}
    }

}