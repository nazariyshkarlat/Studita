package com.studita.presentation.fragments.first_open

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.studita.R
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.fragments.main.MainFragment
import com.studita.utils.addFragment
import com.studita.utils.addFragmentAllowStateLoss
import com.studita.utils.removeFragment
import com.studita.utils.removeFragmentAllowStateLoss
import kotlinx.android.synthetic.main.welcome_layout.*

class WelcomeFragment : BaseFragment(R.layout.welcome_layout){


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.translationZ = Float.MAX_VALUE

        if (savedInstanceState == null) {
            welcomeLayoutText.alpha = 0F
            welcomeLayoutText.animate().alpha(1F).setStartDelay(500).setDuration(
                1500
            ).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    view.animate().alpha(0F).setDuration(500).setStartDelay(1000)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                super.onAnimationEnd(animation)
                                (activity as? AppCompatActivity)?.removeFragmentAllowStateLoss(this@WelcomeFragment)
                            }
                        }).start()

                    (activity as?AppCompatActivity)?.addFragmentAllowStateLoss(
                        MainFragment(),
                        R.id.frameLayout,
                        addToBackStack = false
                    )
                }
            })
        }else {

            if (activity?.supportFragmentManager?.findFragmentById(R.id.frameLayout) !is MainFragment) {
                (activity as AppCompatActivity).addFragmentAllowStateLoss(
                    MainFragment(),
                    R.id.frameLayout,
                    addToBackStack = false
                )
            }

            (activity as AppCompatActivity).removeFragmentAllowStateLoss(this@WelcomeFragment)
        }
    }

}