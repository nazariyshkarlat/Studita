package com.studita.presentation.fragments.main

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.studita.App.Companion.authenticate
import com.studita.R
import com.studita.domain.entity.UserDataData
import com.studita.presentation.activities.MainActivity
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.fragments.dialog_alerts.MainMenuLanguageDialogAlertFragment
import com.studita.presentation.fragments.dialog_alerts.ProfileMenuSignOutDialogAlertFragment
import com.studita.presentation.fragments.friends.MyFriendsFragment
import com.studita.presentation.fragments.friends.MyFriendsOfflineModeFragment
import com.studita.presentation.fragments.notifications.NotificationsFragment
import com.studita.presentation.fragments.notifications.NotificationsOfflineModeFragment
import com.studita.presentation.fragments.privacy.PrivacySettingsFragment
import com.studita.presentation.fragments.privacy.PrivacySettingsOfflineModeFragment
import com.studita.presentation.fragments.profile.MyProfileFragment
import com.studita.presentation.fragments.profile.MyProfileOfflineModeFragment
import com.studita.presentation.fragments.profile.edit.EditProfileFragment
import com.studita.presentation.fragments.profile.edit.EditProfileOfflineModeFragment
import com.studita.presentation.view_model.MainMenuActivityViewModel
import com.studita.presentation.views.press_view.IPressView
import com.studita.utils.*
import com.studita.utils.ThemeUtils.getCurrentTheme
import com.studita.utils.UserUtils.observeNoNull
import kotlinx.android.synthetic.main.main_menu_layout.*
import kotlinx.android.synthetic.main.profile_menu_item.view.*
import kotlinx.android.synthetic.main.profile_menu_layout.*
import kotlinx.android.synthetic.main.settings_switch_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileMenuFragment : NavigatableFragment(R.layout.profile_menu_layout) {

    private var profileMenuItemNotificationsIndicator: View? = null
    private var mainMenuActivityViewModel: MainMenuActivityViewModel? = null
    private var changeThemeJob : Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mainMenuActivityViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainMenuActivityViewModel::class.java)
        }

        if(UserUtils.userDataIsNull()) {
            UserUtils.localUserDataLiveData.observeNoNull(viewLifecycleOwner, Observer {
                fillUserData(it)
            })
        }

        UserUtils.userDataLiveData.observeNoNull(viewLifecycleOwner, {
            fillUserData(it)
            profileMenuItemNotificationsIndicator?.asNotificationIndicator(it.notificationsAreChecked)
        })

        profileMenuLayoutAvatar.setOnClickListener {
            (activity as AppCompatActivity).navigateTo(
                if (PrefsUtils.isOfflineModeEnabled()) MyProfileOfflineModeFragment() else MyProfileFragment(),
                R.id.doubleFrameLayoutFrameLayout
            )
        }

        initSettingsList()

        profileMenuLayoutEditProfile.setOnClickListener {
            (activity as AppCompatActivity).navigateTo(
                if (PrefsUtils.isOfflineModeEnabled()) EditProfileOfflineModeFragment() else EditProfileFragment(),
                R.id.doubleFrameLayoutFrameLayout
            )
        }

        scrollingView = profileMenuLayoutScrollView
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        (profileMenuLayoutSettingsList.getChildAt(ListItems.OFFLINE_MODE.ordinal) as ViewGroup).mainMenuLayoutSwitchItemSwitch.isChecked = PrefsUtils.isOfflineModeEnabled()
    }

    private fun fillUserData(userDataData: UserDataData) {
        profileMenuLayoutUserName.text =
            resources.getString(R.string.user_name_template, userDataData.userName)
        profileMenuLayoutAvatar.fillAvatar(userDataData.avatarLink, userDataData.userName!!, userDataData.userId!!)
    }

    private fun getProfileMenuItems() = listOf(
        R.drawable.ic_person_secondary to resources.getString(R.string.my_profile),
        R.drawable.ic_notifications_secondary to resources.getString(R.string.notifications),
        R.drawable.ic_people_secondary to resources.getString(R.string.my_friends),
        R.drawable.ic_lock_secondary to resources.getString(R.string.privacy),
        R.drawable.ic_cloud_secondary to resources.getString(R.string.offline_mode),
        R.drawable.ic_palette_secondary to resources.getString(R.string.app_theme),
        R.drawable.ic_language_secondary to resources.getString(R.string.language),
        R.drawable.ic_exit_to_app_secondary to resources.getString(R.string.sign_out)
    )

    private fun initSettingsList() {
        getProfileMenuItems().forEachIndexed { index, pair ->
            val itemView: View
            when (index) {
                ListItems.OFFLINE_MODE.ordinal, ListItems.THEME.ordinal -> {
                    itemView =
                        profileMenuLayoutSettingsList.makeView(R.layout.settings_switch_item)

                    with(itemView) {
                        if (index == ListItems.OFFLINE_MODE.ordinal) {
                            mainMenuLayoutSwitchItemSwitch.isChecked =
                                PrefsUtils.isOfflineModeEnabled()

                            mainMenuLayoutSwitchItemIcon.setImageResource(R.drawable.ic_cloud_secondary)
                            mainMenuLayoutSwitchItemTextView.text = resources.getString(R.string.offline_mode)
                        }else{
                            mainMenuLayoutSwitchItemSwitch.isChecked = activity!!.getCurrentTheme() == ThemeUtils.Theme.DARK

                            mainMenuLayoutSwitchItemIcon.setImageResource(R.drawable.ic_palette_secondary)
                            mainMenuLayoutSwitchItemTextView.text = resources.getString(R.string.dark_theme)
                        }
                        mainMenuLayoutSwitchItemSwitchView.setListOnClick(index)
                    }
                }
                else -> {
                    itemView =
                        profileMenuLayoutSettingsList.makeView(R.layout.profile_menu_item).apply {
                            if (index == ListItems.LANGUAGE.ordinal || index == ListItems.THEME.ordinal || index == ListItems.SIGN_OUT.ordinal)
                                profileMenuItemLayout.setWithMinClickInterval(true)
                        }

                    with(itemView) ItemView@{

                        if (index == ListItems.NOTIFICATIONS.ordinal) {

                            with(View(context).apply {
                                background = ContextCompat.getDrawable(
                                    context,
                                    R.drawable.new_notifications_indicator
                                )
                                visibility = View.GONE
                            }) {

                                profileMenuItemNotificationsIndicator = this
                                this@ItemView.profileMenuItemIconLayout.addView(this)

                                this@ItemView.profileMenuItemIconLayout.post {
                                    this.updateLayoutParams<FrameLayout.LayoutParams> {
                                        gravity = Gravity.TOP or Gravity.END
                                        height = 12F.dpToPx()
                                        width = 12F.dpToPx()
                                    }
                                }
                            }

                        }
                        profileMenuItemIcon.setImageResource(pair.first)
                        profileMenuItemText.text = pair.second
                        profileMenuItemLayout.setListOnClick(index)
                    }
                }
            }
            itemView.setListDividers(index)
            profileMenuLayoutSettingsList.addView(itemView)
        }
    }

    private fun View.setListDividers(position: Int) {
        if (position == ListItems.MY_PROFILE.ordinal || position == ListItems.PRIVACY.ordinal) {
            setPadding(0, 12F.dpToPx(), 0, 0)
            background =
                androidx.core.content.ContextCompat.getDrawable(
                    context,
                    R.drawable.divider_top_drawable
                )
        }
        if (position == ListItems.FRIENDS.ordinal) {
            setPadding(0, 0, 0, 12F.dpToPx())
        }
    }

    private fun IPressView.setListOnClick(position: Int) {
        setOnClickListener {
            when (position) {
                ListItems.MY_PROFILE.ordinal -> {
                    (activity as AppCompatActivity).navigateTo(
                        if (PrefsUtils.isOfflineModeEnabled()) MyProfileOfflineModeFragment() else MyProfileFragment(),
                        R.id.doubleFrameLayoutFrameLayout
                    )
                }
                ListItems.THEME.ordinal -> {

                    if(changeThemeJob == null ||
                        changeThemeJob?.isActive == false){
                        with((profileMenuLayoutSettingsList.getChildAt(ListItems.THEME.ordinal) as ViewGroup)) {

                            mainMenuLayoutSwitchItemSwitch.isChecked =
                                !mainMenuLayoutSwitchItemSwitch.isChecked

                            changeThemeJob = lifecycleScope.launch(Dispatchers.Main) {
                                delay(200)
                                changeTheme(mainMenuLayoutSwitchItemSwitch.isChecked)
                            }
                        }
                    }
                }
                ListItems.LANGUAGE.ordinal -> {
                    MainMenuLanguageDialogAlertFragment()
                        .show((activity as AppCompatActivity).supportFragmentManager, null)
                }
                ListItems.SIGN_OUT.ordinal -> {
                    ProfileMenuSignOutDialogAlertFragment().show(
                        (activity as AppCompatActivity).supportFragmentManager,
                        null
                    )
                }
                ListItems.OFFLINE_MODE.ordinal -> {
                    with((profileMenuLayoutSettingsList.getChildAt(ListItems.OFFLINE_MODE.ordinal) as ViewGroup)) {
                        PrefsUtils.setOfflineMode(!mainMenuLayoutSwitchItemSwitch.isChecked)
                        mainMenuLayoutSwitchItemSwitch.isChecked =
                            !mainMenuLayoutSwitchItemSwitch.isChecked

                        authenticate(UserUtils.getUserIDTokenData(), true)
                    }
                }
                ListItems.PRIVACY.ordinal -> {
                    (activity as AppCompatActivity).navigateTo(
                        if (PrefsUtils.isOfflineModeEnabled()) PrivacySettingsOfflineModeFragment() else PrivacySettingsFragment(),
                        R.id.doubleFrameLayoutFrameLayout
                    )
                }
                ListItems.FRIENDS.ordinal -> {
                    (activity as AppCompatActivity).navigateTo(
                        if (PrefsUtils.isOfflineModeEnabled()) MyFriendsOfflineModeFragment() else MyFriendsFragment(),
                        R.id.doubleFrameLayoutFrameLayout
                    )
                }
                ListItems.NOTIFICATIONS.ordinal -> {
                    (activity as AppCompatActivity).navigateTo(
                        if (PrefsUtils.isOfflineModeEnabled()) NotificationsOfflineModeFragment() else NotificationsFragment(),
                        R.id.doubleFrameLayoutFrameLayout
                    )
                }
            }
        }
    }

    private fun changeTheme(darkModeEnabled: Boolean) {
        mainMenuActivityViewModel?.onThemeChangeListener?.onThemeChanged(ThemeUtils.Theme.values()[if(darkModeEnabled) 0 else 1])
        MainActivity.needsRefresh = true
    }

    enum class ListItems {
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