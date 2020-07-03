package com.example.studita.presentation.fragments

import android.os.Bundle
import android.preference.PreferenceGroup
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.studita.R
import com.example.studita.domain.entity.UserDataData
import com.example.studita.presentation.draw.AvaDrawer
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.dialog_alerts.MainMenuLanguageDialogAlertFragment
import com.example.studita.presentation.fragments.dialog_alerts.MainMenuThemeDialogAlertFragment
import com.example.studita.presentation.fragments.dialog_alerts.ProfileMenuSignOutDialogAlertFragment
import com.example.studita.presentation.view_model.ProfileMenuFragmentViewModel
import com.example.studita.presentation.views.press_view.IPressView
import com.example.studita.utils.*
import kotlinx.android.synthetic.main.profile_menu_layout.*
import kotlinx.android.synthetic.main.settings_item.view.*
import kotlinx.android.synthetic.main.settings_offline_mode_item.*
import kotlinx.android.synthetic.main.settings_offline_mode_item.view.*

class ProfileMenuFragment : NavigatableFragment(R.layout.profile_menu_layout){

    private var profileMenuFragmentViewModel: ProfileMenuFragmentViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileMenuFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(ProfileMenuFragmentViewModel::class.java)
        }

        UserUtils.userDataLiveData.observe(activity as FragmentActivity, Observer {
            fillUserData(it)
        })

        initSettingList()

        profileMenuLayoutEditProfile.setOnClickListener {
            (activity as AppCompatActivity).navigateTo(if(PrefsUtils.isOfflineMode()) EditProfileOfflineModeFragment() else EditProfileFragment(), R.id.doubleFrameLayoutFrameLayout)
        }

        scrollingView = profileMenuLayoutScrollView
    }

    private fun fillUserData(userDataData: UserDataData){
            profileMenuLayoutUserName.text =
                resources.getString(R.string.user_name_template, userDataData.userName)
            fillAvatar(userDataData)
    }

    private fun fillAvatar(userDataData: UserDataData){
        if (userDataData.avatarLink == null) {
            AvaDrawer.drawAvatar(profileMenuLayoutAvatar, UserUtils.userData.userName!!, PrefsUtils.getUserId()!!)
        } else {
            Glide
                .with(this)
                .load(userDataData.avatarLink)
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .into(profileMenuLayoutAvatar)
        }
    }

    private fun getSettingsItems() = listOf(R.drawable.ic_person_secondary to resources.getString(R.string.my_profile),
        R.drawable.ic_notifications_secondary to resources.getString(R.string.notifications),
            R.drawable.ic_people_secondary to resources.getString(R.string.my_friends),
            R.drawable.ic_lock_secondary to resources.getString(R.string.privacy),
            R.drawable.ic_cloud_secondary to resources.getString(R.string.offline_mode),
            R.drawable.ic_palette_secondary to resources.getString(R.string.app_theme),
            R.drawable.ic_language_secondary to resources.getString(R.string.language),
            R.drawable.ic_exit_to_app_secondary to resources.getString(R.string.sign_out))

    private fun initSettingList(){
        getSettingsItems().forEachIndexed { index, pair ->
            val itemView: View
            if(index != ListItems.OFFLINE_MODE.ordinal) {
                itemView = profileMenuLayoutSettingsList.makeView(R.layout.settings_item)
                with(itemView) {
                    settingsItemIcon.setImageResource(pair.first)
                    settingsItemText.text = pair.second
                    settingsItemLayout.setListOnClick(index)
                }
            }else{
                itemView = profileMenuLayoutSettingsList.makeView(R.layout.settings_offline_mode_item)
                with(itemView) {
                    mainMenuLayoutOfflineSwitch.isChecked = PrefsUtils.isOfflineMode()
                    mainMenuLayoutOfflineSwitchView.setListOnClick(index)
                }
            }
            itemView.setListDividers(index)
            profileMenuLayoutSettingsList.addView(itemView)
        }
    }

    private fun View.setListDividers(position: Int){
        if (position == ListItems.MY_PROFILE.ordinal || position == ListItems.PRIVACY.ordinal) {
            setPadding(0, 12.dpToPx(), 0, 0)
            background =
                androidx.core.content.ContextCompat.getDrawable(context, R.drawable.divider_top_drawable)
        }
        if(position == ListItems.FRIENDS.ordinal){
            setPadding(0, 0, 0, 12.dpToPx())
        }
    }

    private fun IPressView.setListOnClick(position: Int){
        setOnClickListener {
            when (position) {
                ListItems.MY_PROFILE.ordinal -> {
                    (activity as AppCompatActivity).navigateTo(if(PrefsUtils.isOfflineMode())  MyProfileOfflineModeFragment() else MyProfileFragment(), R.id.doubleFrameLayoutFrameLayout)
                }
                ListItems.THEME.ordinal -> {
                    MainMenuThemeDialogAlertFragment().show(
                        (activity as AppCompatActivity).supportFragmentManager,
                        null
                    )
                }
                ListItems.LANGUAGE.ordinal -> {
                    MainMenuLanguageDialogAlertFragment()
                        .show((activity as AppCompatActivity).supportFragmentManager, null)
                }
                ListItems.SIGN_OUT.ordinal ->{
                    ProfileMenuSignOutDialogAlertFragment().show((activity as AppCompatActivity).supportFragmentManager, null)
                }
                ListItems.OFFLINE_MODE.ordinal ->{
                    PrefsUtils.setOfflineMode(!mainMenuLayoutOfflineSwitch.isChecked)
                    mainMenuLayoutOfflineSwitch.isChecked = !mainMenuLayoutOfflineSwitch.isChecked
                }
                ListItems.PRIVACY.ordinal ->{
                    (activity as AppCompatActivity).navigateTo(if(PrefsUtils.isOfflineMode()) PrivacySettingsOfflineModeFragment() else PrivacySettingsFragment(), R.id.doubleFrameLayoutFrameLayout)
                }
                ListItems.FRIENDS.ordinal ->{
                    (activity as AppCompatActivity).navigateTo(if(PrefsUtils.isOfflineMode()) MyProfileOfflineModeFragment() else MyFriendsFragment(), R.id.doubleFrameLayoutFrameLayout)
                }
                ListItems.NOTIFICATIONS.ordinal->{
                    (activity as AppCompatActivity).navigateTo(if(PrefsUtils.isOfflineMode()) NotificationsOfflineModeFragment() else NotificationsFragment(), R.id.doubleFrameLayoutFrameLayout)
                }
            }
        }
    }

    enum class ListItems{
        MY_PROFILE,
        NOTIFICATIONS,
        FRIENDS,
        PRIVACY,
        OFFLINE_MODE,
        THEME,
        LANGUAGE,
        SIGN_OUT
    }

}