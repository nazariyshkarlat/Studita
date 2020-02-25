package com.example.studita.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.extensions.navigateTo
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.MainMenuActivityViewModel
import com.example.studita.presentation.view_model.MainMenuFragmentViewModel
import kotlinx.android.synthetic.main.exercise_variants_fragment.*
import kotlinx.android.synthetic.main.main_menu_layout.*

class MainMenuFragment : NavigatableFragment(R.layout.main_menu_layout){

    private var fragmentViewModel: MainMenuFragmentViewModel? = null
    private var activityViewModel: MainMenuActivityViewModel? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainMenuFragmentViewModel::class.java)
        }

        activityViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainMenuActivityViewModel::class.java)
        }

        fragmentViewModel?.let{viewModel->
            viewModel.signUpMethodState.observe(
                viewLifecycleOwner,
            androidx.lifecycle.Observer{ state->
                if(state == MainMenuFragmentViewModel.SignUpMehod.WITH_GOOGLE){

                }else{
                    (activity as AppCompatActivity).navigateTo(AuthorizationFragment(), R.id.doubleConstraintFrameLayout)
                }
            })
            mainMenuWithGoogleButton.setOnClickListener {viewModel.onSignUpLogInClick(it.id)}
            mainMenuUseEmailButton.setOnClickListener {viewModel.onSignUpLogInClick(it.id)}
        }

        checkScrollable()
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            checkScrollable()
        }
    }

    private fun checkScrollable() {
        OneShotPreDrawListener.add(mainMenuScrollView) {
            if(mainMenuScrollView.height < mainMenuScrollView.getChildAt(0).height
                + mainMenuScrollView.paddingTop + mainMenuScrollView.paddingBottom){
                mainMenuTitle.visibility = View.GONE
                activityViewModel?.setToolbarText(mainMenuTitle.text.toString())
            }

        }
    }

}