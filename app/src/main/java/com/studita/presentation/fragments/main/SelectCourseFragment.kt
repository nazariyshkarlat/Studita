package com.studita.presentation.fragments.main

import android.os.Bundle
import android.text.Editable
import android.text.method.KeyListener
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import com.studita.R
import com.studita.presentation.activities.MainMenuActivity
import com.studita.presentation.view_model.SelectCourseFragmentViewModel
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.*
import kotlinx.android.synthetic.main.select_course_layout.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


class SelectCourseFragment : com.studita.presentation.fragments.base.BaseFragment(R.layout.select_course_layout){

    val viewModel: SelectCourseFragmentViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = activity!!

        viewModel.subscribeEmailEvent.observe(
            context,
            androidx.lifecycle.Observer { status ->
                showSubscribeEmailSnackbar(status, context, onMainLayout = this.context == null)
            })

        viewModel.subscribeEmailErrorEvent.observe(
            context,
            androidx.lifecycle.Observer {
                CustomSnackbar(context).show(
                    resources.getString(R.string.server_temporarily_unavailable),
                    ThemeUtils.getRedColor(context),
                    ContextCompat.getColor(context, R.color.white),
                    bottomMarginExtra = if (this.context != null) 0 else resources.getDimension(R.dimen.bottomNavigationHeight)
                        .toInt()
                )
            })

        selectCourseLayoutMotionLayout.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                if (selectCourseLayoutMotionLayout.progress == 0F)
                    fragmentManager?.removeFragment(this@SelectCourseFragment)
            }
        })

        selectCourseLayoutMotionLayout.transitionToEnd()
        selectCourseLayoutTitle.animateExpandIcon(backwards = false)

        selectCourseLayoutTitle.setOnClickListener {
            selectCourseLayoutTitle.animateExpandIcon(backwards = true)
            selectCourseLayoutMotionLayout.transitionToStart()
        }

        selectCourseLayoutButton.setOnClickListener {
            selectCourseLayoutMotionLayout.transitionToStart()
        }

        if(UserUtils.isLoggedIn()){
            UserUtils.userDataLiveData.observe(viewLifecycleOwner, { userData ->
                selectCourseLayoutSubscribeText.text =
                    resources.getString(R.string.subscribe_email_text)
                if (userData.isSubscribed) {
                    selectCourseLayoutSubscribeButton.text =
                        resources.getString(R.string.unsubscribe_email_button_text)

                    selectCourseLayoutSubscribeButton.setOnClickListener {
                        viewModel.subscribeEmail(
                            UserUtils.getUserIDTokenData()!!,
                            subscribe = false
                        )
                    }
                } else {
                    selectCourseLayoutSubscribeButton.text =
                        resources.getString(R.string.subscribe_email_button_text)
                    selectCourseLayoutSubscribeButton.setOnClickListener {
                        viewModel.subscribeEmail(UserUtils.getUserIDTokenData()!!, subscribe = true)
                    }
                }
            })
        }else{
            selectCourseLayoutSubscribeText.text = resources.getString(R.string.select_course_sign_in_text)
            selectCourseLayoutSubscribeButton.text = resources.getString(R.string.sign_in)
            selectCourseLayoutSubscribeButton.setOnClickListener {
                activity?.startActivity<MainMenuActivity>()
            }
        }

        this.view?.isFocusableInTouchMode = true
        this.view?.requestFocus()
        this.view?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                selectCourseLayoutMotionLayout.transitionToStart()
                return@setOnKeyListener true
            }
            false
        }
    }

}