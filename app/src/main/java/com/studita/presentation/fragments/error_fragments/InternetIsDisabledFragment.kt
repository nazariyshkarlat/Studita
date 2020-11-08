package com.studita.presentation.fragments.error_fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import com.studita.R
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.studita.presentation.listeners.ReloadPageCallback
import com.studita.utils.removeFragment
import com.studita.utils.removeFragmentWithAnimation
import kotlinx.android.synthetic.main.internet_is_disabled_layout.*

class InternetIsDisabledFragment : BaseFragment(R.layout.internet_is_disabled_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        internetIsDisabledLayoutButton.setOnSingleClickListener {
            removeFragmentWithAnimation(view)
            (parentFragment as? ReloadPageCallback)?.onPageReload()
        }

        view.setOnTouchListener { _, _ ->  true}
    }

}