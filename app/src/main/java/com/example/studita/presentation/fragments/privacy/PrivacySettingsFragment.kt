package com.example.studita.presentation.fragments.privacy

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.DuelsInvitesFrom
import com.example.studita.domain.entity.EditDuelsExceptionsData
import com.example.studita.domain.entity.PrivacySettingsData
import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.dialog_alerts.PrivacySettingsEditExceptionsDialogAlertFragment
import com.example.studita.presentation.fragments.error_fragments.InternetIsDisabledFragment
import com.example.studita.presentation.fragments.error_fragments.ServerProblemsFragment
import com.example.studita.presentation.listeners.ReloadPageCallback
import com.example.studita.presentation.view_model.PrivacySettingsViewModel
import com.example.studita.presentation.views.CustomSnackbar
import com.example.studita.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.privacy_settings_layout.*


class PrivacySettingsFragment : NavigatableFragment(R.layout.privacy_settings_layout), ReloadPageCallback {

    private lateinit var privacySettingsViewModel: PrivacySettingsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        privacySettingsViewModel =
            ViewModelProviders.of(this).get(PrivacySettingsViewModel::class.java)

        privacySettingsViewModel.let { viewModel ->

            viewModel.errorEvent.observe(viewLifecycleOwner, Observer { isNetworkError->
                if (isNetworkError) {
                    addFragment(InternetIsDisabledFragment(), R.id.privacySettingsLayoutFrameLayout, false)
                }else{
                    addFragment(ServerProblemsFragment(), R.id.privacySettingsLayoutFrameLayout, false)
                }
            })

            val context = activity as AppCompatActivity

            viewModel.errorSnackbarEvent.observe(context, Observer {
                if(it){
                    CustomSnackbar(context).show(
                        context.resources.getString(R.string.server_temporarily_unavailable),
                        ThemeUtils.getRedColor(context)
                    )
                }
            })

            viewModel.privacySettingsStatus.observe(
                viewLifecycleOwner,
                Observer { privacySettings ->

                    (view as ViewGroup).removeView(privacySettingsLayoutProgressBar)

                    privacySettingsLayoutScrollView.visibility = View.VISIBLE

                    privacySettings.duelsInvitesFrom?.let { setDuelsInvitesFormCheckboxes(it) }

                    val duelsExceptions = privacySettings.duelsExceptions
                    formExceptionsView(duelsExceptions)

                    if (privacySettings.showInRatings == true) {
                        privacySettingsLayoutRatingsBlockShowRadio.isChecked = true
                        privacySettingsLayoutRatingsBlockShowRadio.jumpDrawablesToCurrentState()
                    } else {
                        privacySettingsLayoutRatingsBlockHideRadio.isChecked = true
                        privacySettingsLayoutRatingsBlockHideRadio.jumpDrawablesToCurrentState()
                    }

                    if (privacySettings.profileIsVisible == true) {
                        privacySettingsLayoutVisibilityBlockShowRadio.isChecked = true
                        privacySettingsLayoutVisibilityBlockShowRadio.jumpDrawablesToCurrentState()
                    } else {
                        privacySettingsLayoutVisibilityBlockHideRadio.isChecked = true
                        privacySettingsLayoutVisibilityBlockHideRadio.jumpDrawablesToCurrentState()
                    }

                    view.setRadioButtonsListeners(viewModel)
                })


            privacySettingsContentLayout.forEach { parent ->
                parent as ViewGroup
                parent.forEach { view ->
                    if (view is ViewGroup) {
                        view.setOnClickListener { child ->
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

        privacySettingsLayoutDuelsBlockEditExceptions.setOnClickListener {
            PrivacySettingsEditExceptionsDialogAlertFragment().apply {
                setTargetFragment(this@PrivacySettingsFragment, 0)
            }.show((activity as AppCompatActivity).supportFragmentManager, null)
        }

        scrollingView = privacySettingsLayoutScrollView
        disableUnusedItems()
    }

    private fun ViewGroup.setRadioButtonsListeners(viewModel: PrivacySettingsViewModel) {
        this.allViewsOfTypeT<RadioButton> {
            it.setOnCheckedChangeListener { view, isChecked ->
                if (isChecked) {
                    viewModel.editPrivacySettings(
                        PrivacySettingsRequestData(
                            UserUtils.getUserIDTokenData()!!, when (view.id) {
                                R.id.privacySettingsLayoutDuelsBlockMyFriendsRadio -> PrivacySettingsData(
                                    duelsInvitesFrom = DuelsInvitesFrom.FRIENDS
                                )
                                R.id.privacySettingsLayoutDuelsBlockNobodyRadio -> PrivacySettingsData(
                                    duelsInvitesFrom = DuelsInvitesFrom.NOBODY
                                )
                                R.id.privacySettingsLayoutDuelsBlockExceptRadio -> PrivacySettingsData(
                                    duelsInvitesFrom = DuelsInvitesFrom.EXCEPT
                                )
                                R.id.privacySettingsLayoutRatingsBlockShowRadio -> PrivacySettingsData(
                                    showInRatings = true
                                )
                                R.id.privacySettingsLayoutRatingsBlockHideRadio -> PrivacySettingsData(
                                    showInRatings = false
                                )
                                R.id.privacySettingsLayoutVisibilityBlockShowRadio -> PrivacySettingsData(
                                    profileIsVisible = true
                                )
                                R.id.privacySettingsLayoutVisibilityBlockHideRadio -> PrivacySettingsData(
                                    profileIsVisible = false
                                )
                                else -> {
                                    PrivacySettingsData()
                                }
                            }
                        )
                    )
                }
            }
        }
    }

    private fun formExceptionsView(duelsExceptions: ArrayList<String>?) {
        if (privacySettingsViewModel.hasFriends) {
            privacySettingsLayoutDuelsBlockEditExceptions.visibility = View.VISIBLE
            if (duelsExceptions != null && duelsExceptions.isNotEmpty()) {
                privacySettingsLayoutDuelsBlockEditExceptions.text =
                    resources.getString(R.string.privacy_settings_edit_duels_exceptions)
                privacySettingsLayoutDuelsBlockExcept.visibility = View.VISIBLE
                privacySettingsLayoutDuelsBlockExceptText.text = when {
                    duelsExceptions.size == 1 -> {
                        resources.getString(
                            R.string.privacy_settings_first_from_nobody_except,
                            "@${duelsExceptions[0]}"
                        )
                    }
                    duelsExceptions.size == 2 -> {
                        resources.getString(
                            R.string.privacy_settings_first_from_nobody_except_two,
                            "@${duelsExceptions[0]}",
                            "@${duelsExceptions[1]}"
                        )
                    }
                    duelsExceptions.size > 2 -> {
                        resources.getString(
                            R.string.privacy_settings_first_from_nobody_except_many,
                            "@${duelsExceptions[0]}",
                            duelsExceptions.size - 1
                        )
                    }
                    else -> null
                }
            } else {
                privacySettingsLayoutDuelsBlockExcept.visibility = View.GONE
                privacySettingsLayoutDuelsBlockEditExceptions.text =
                    resources.getString(R.string.privacy_settings_add_duels_exceptions)
            }
        }
    }

    private fun setDuelsInvitesFormCheckboxes(duelsInvitesFrom: DuelsInvitesFrom) {

        privacySettingsLayoutDuelsBlockMyFriendsRadio.isChecked = false
        privacySettingsLayoutDuelsBlockNobodyRadio.isChecked = false
        privacySettingsLayoutDuelsBlockExceptRadio.isChecked = false

        when (duelsInvitesFrom) {
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

        privacySettingsLayoutDuelsBlockMyFriendsRadio.jumpDrawablesToCurrentState()
        privacySettingsLayoutDuelsBlockNobodyRadio.jumpDrawablesToCurrentState()
        privacySettingsLayoutDuelsBlockExceptRadio.jumpDrawablesToCurrentState()
    }

    private fun disableUnusedItems(){
        privacySettingsLayoutDuelsBlock.disableAllItems()
        privacySettingsLayoutRatingsBlock.disableAllItems()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 345) {
            if (data?.extras?.containsKey("CHANGED_EXCEPTIONS") == true) {
                val json = data.getStringExtra("CHANGED_EXCEPTIONS") as String
                val changedExceptions = Gson().fromJson<ArrayList<EditDuelsExceptionsData>>(
                    json,
                    object : TypeToken<ArrayList<EditDuelsExceptionsData>>() {}.type
                )

                val privacySettingsData = privacySettingsViewModel.privacySettingsStatus.value

                if (privacySettingsData != null) {

                    changedExceptions.forEach { exceptionData ->
                        if (exceptionData.deleteFromExceptions)
                            privacySettingsData.duelsExceptions?.removeAll { it == exceptionData.userName }
                        else
                            privacySettingsData.duelsExceptions?.add(
                                exceptionData.userName
                            )
                    }

                    val wasExcept = privacySettingsData.duelsInvitesFrom == DuelsInvitesFrom.EXCEPT

                    if (privacySettingsData.duelsExceptions?.isEmpty() == true && privacySettingsData.duelsInvitesFrom == DuelsInvitesFrom.EXCEPT)
                        privacySettingsData.duelsInvitesFrom = DuelsInvitesFrom.NOBODY
                    else
                        privacySettingsData.duelsInvitesFrom = DuelsInvitesFrom.EXCEPT

                    if(!wasExcept) {
                        privacySettingsData.duelsInvitesFrom?.let {
                            setDuelsInvitesFormCheckboxes(
                                it
                            )
                        }
                    }

                    privacySettingsData.duelsExceptions?.let {
                        formExceptionsView(
                            it
                        )
                    }
                }
            }
        }
    }

    override fun onPageReload() {
        privacySettingsViewModel.getPrivacySettings(UserUtils.getUserIDTokenData()!!)
    }
}