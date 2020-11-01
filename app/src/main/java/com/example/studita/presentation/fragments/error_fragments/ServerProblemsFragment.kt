package com.example.studita.presentation.fragments.error_fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.example.studita.presentation.listeners.ReloadPageCallback
import com.example.studita.utils.removeFragment
import com.example.studita.utils.removeFragmentWithAnimation
import kotlinx.android.synthetic.main.server_problems_layout.*

class ServerProblemsFragment : BaseFragment(R.layout.server_problems_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serverProblemsLayoutButton.setOnSingleClickListener {
            removeFragmentWithAnimation(view)
            (parentFragment as? ReloadPageCallback)?.onPageReload()
        }

        view.setOnTouchListener { _, _ ->  true}
    }

}