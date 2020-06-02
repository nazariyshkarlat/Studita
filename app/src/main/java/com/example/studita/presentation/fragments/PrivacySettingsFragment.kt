package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.DuelsInvitesFrom
import com.example.studita.domain.entity.PrivacySettingsData
import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.PrivacySettingsViewModel
import com.example.studita.utils.UserUtils
import com.example.studita.utils.allViewsOfTypeT
import kotlinx.android.synthetic.main.privacy_settings_layout.*


class PrivacySettingsFragment : NavigatableFragment(R.layout.privacy_settings_layout){

    private var privacySettingsViewModel: PrivacySettingsViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        privacySettingsViewModel = ViewModelProviders.of(this).get(PrivacySettingsViewModel::class.java)

        privacySettingsViewModel?.let{viewModel->

            viewModel.errorState.observe(viewLifecycleOwner, Observer { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            })


            viewModel.privacySettingsStatus.observe(viewLifecycleOwner, Observer {privacySettings->

                (view as ViewGroup).removeView(privacySettingsLayoutProgressBar)

                privacySettingsLayoutScrollView.visibility = View.VISIBLE

                when(privacySettings.duelsInvitesFrom){
                    DuelsInvitesFrom.FRIENDS -> {
                        privacySettingsLayoutDuelsBlockMyFriendsRadio.isChecked = true
                    }
                    DuelsInvitesFrom.NOBODY -> {
                        privacySettingsLayoutDuelsBlockNobodyRadio.isChecked = true
                    }
                    DuelsInvitesFrom.EXCEPT -> {
                        privacySettingsLayoutDuelsBlockExceptRadio.isChecked = true
                    }
                }

                val duelsExceptions = privacySettings.duelsExceptions
                if(duelsExceptions != null && duelsExceptions.isNotEmpty()) {
                    privacySettingsLayoutDuelsBlockExcept.visibility = View.VISIBLE
                    privacySettingsLayoutDuelsBlockExceptText.text = when {
                        duelsExceptions.size == 1 -> {
                            resources.getString(R.string.privacy_settings_first_from_nobody_except,
                                    "@${duelsExceptions[0]}")
                        }
                        duelsExceptions.size == 2 -> {
                            resources.getString(R.string.privacy_settings_first_from_nobody_except_two,
                                    "@${duelsExceptions[0]}",
                                    "@${duelsExceptions[1]}")
                        }
                        duelsExceptions.size > 2 -> {
                            resources.getString(R.string.privacy_settings_first_from_nobody_except_many,
                                    "@${duelsExceptions[0]}",
                                    duelsExceptions.size - 1)
                        }
                        else -> null
                    }
                }

                if(privacySettings.showInRatings == true)
                    privacySettingsLayoutRatingsBlockShowRadio.isChecked = true
                else
                    privacySettingsLayoutRatingsBlockHideRadio.isChecked = true

                if(privacySettings.profileIsVisible == true)
                    privacySettingsLayoutVisibilityBlockShowRadio.isChecked = true
                else
                    privacySettingsLayoutVisibilityBlockHideRadio.isChecked = true

                view.setRadioButtonsListeners(viewModel)
            })


            privacySettingsContentLayout.forEach {parent->
                parent as ViewGroup
                parent.forEach{view->
                    if(view is ViewGroup){
                        view.setOnClickListener {child->
                            parent.forEach {
                                if (it is ViewGroup) {
                                    (it.getChildAt(1) as RadioButton).isChecked = it == child
                                }
                            }
                        }
                    }
                }
            }
        }
        scrollingView = privacySettingsLayoutScrollView
    }

    private fun ViewGroup.setRadioButtonsListeners(viewModel: PrivacySettingsViewModel){
        this.allViewsOfTypeT<RadioButton> {
            it.setOnCheckedChangeListener { view, isChecked ->
                if (isChecked) {
                    viewModel.editPrivacySettings(PrivacySettingsRequestData(UserUtils.getUserIDTokenData()!!, when (view.id) {
                        R.id.privacySettingsLayoutDuelsBlockMyFriendsRadio -> PrivacySettingsData(duelsInvitesFrom = DuelsInvitesFrom.FRIENDS)
                        R.id.privacySettingsLayoutDuelsBlockNobodyRadio -> PrivacySettingsData(duelsInvitesFrom = DuelsInvitesFrom.NOBODY)
                        R.id.privacySettingsLayoutDuelsBlockExceptRadio -> PrivacySettingsData(duelsInvitesFrom = DuelsInvitesFrom.EXCEPT)
                        R.id.privacySettingsLayoutRatingsBlockShowRadio -> PrivacySettingsData(showInRatings = true)
                        R.id.privacySettingsLayoutRatingsBlockHideRadio -> PrivacySettingsData(showInRatings = false)
                        R.id.privacySettingsLayoutVisibilityBlockShowRadio -> PrivacySettingsData(profileIsVisible = true)
                        R.id.privacySettingsLayoutVisibilityBlockHideRadio -> PrivacySettingsData(profileIsVisible = false)
                        else -> {PrivacySettingsData()}
                    }))
                }
            }
        }
    }

}