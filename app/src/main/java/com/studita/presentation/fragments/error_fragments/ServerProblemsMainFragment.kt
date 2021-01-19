package com.studita.presentation.fragments.error_fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.drawable.RotateDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.lifecycleScope
import com.studita.App
import com.studita.R
import com.studita.presentation.fragments.achievements.AchievementsFragment
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.fragments.main.HomeFragment
import com.studita.presentation.fragments.main.MainFragment.Companion.getBottomMarginExtraSnackbar
import com.studita.presentation.fragments.main.MainFragment.Companion.showEnableOfflineModeSnackbar
import com.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.studita.presentation.listeners.ReloadPageCallback
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.*
import kotlinx.android.synthetic.main.internet_is_disabled_main_layout.*
import kotlinx.android.synthetic.main.server_problems_main_layout.*
import kotlinx.coroutines.*

class ServerProblemsMainFragment : BaseFragment(R.layout.server_problems_main_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState == null) {
            (parentFragment as? HomeFragment)?.homeFragmentViewModel?.getOfflineLevels()
        }

        when (parentFragment) {
            is HomeFragment -> {
                serverProblemsMainLayoutSubtitle.text = resources.getString(R.string.server_problems_home_layout_subtitle)
            }
            is AchievementsFragment -> {
                serverProblemsMainLayoutSubtitle.text = resources.getString(R.string.server_problems_achievements_layout_subtitle)
            }
        }

        serverProblemsMainLayoutTryAgainButton.setOnSingleClickListener {
            serverProblemsMainLayoutEnableOfflineModeButton.setOnClickListener {  }
            serverProblemsMainLayoutTryAgainButton.animateRefreshButton()
            lifecycleScope.launch(Dispatchers.Main) {
                delay(400)
                (parentFragment as? ReloadPageCallback)?.onPageReload()
                fragmentManager!!.removeFragment(this@ServerProblemsMainFragment)
            }
        }

        if(parentFragment is HomeFragment) {
            serverProblemsMainLayoutEnableOfflineModeButton.setOnClickListener {
                serverProblemsMainLayoutTryAgainButton.setOnClickListener { }
                PrefsUtils.setOfflineMode(true)
                (parentFragment as? ReloadPageCallback)?.onPageReload()
                removeFragmentWithAnimation(view)
                showEnableOfflineModeSnackbar(context!!)
            }
        }else{
            serverProblemsMainLayoutEnableOfflineModeButton.visibility = View.GONE
        }

        view.setOnTouchListener { _, _ ->  true}
    }
}