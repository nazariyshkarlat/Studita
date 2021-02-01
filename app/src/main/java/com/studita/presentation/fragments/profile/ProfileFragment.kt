package com.studita.presentation.fragments.profile

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.studita.R
import com.studita.domain.entity.UserData
import com.studita.domain.entity.UserDataData
import com.studita.domain.entity.UsersResponseData
import com.studita.domain.entity.toUserData
import com.studita.domain.interactor.GetUsersStatus
import com.studita.domain.interactor.IsMyFriendStatus
import com.studita.domain.interactor.UserDataStatus
import com.studita.domain.interactor.users.UsersInteractor
import com.studita.presentation.activities.promo.AchievementsActivity
import com.studita.presentation.activities.promo.CompetitionsActivity
import com.studita.presentation.fragments.achievements.AchievementsFragment
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.fragments.error_fragments.InternetIsDisabledFragment
import com.studita.presentation.fragments.error_fragments.ServerProblemsFragment
import com.studita.presentation.fragments.friends.FriendsFragment
import com.studita.presentation.fragments.friends.MyFriendsFragment
import com.studita.presentation.fragments.profile.edit.EditProfileFragment
import com.studita.presentation.fragments.user_statistics.UserStatFragment
import com.studita.presentation.listeners.ReloadPageCallback
import com.studita.presentation.model.AchievementUiModel
import com.studita.presentation.view_model.ProfileFragmentViewModel
import com.studita.presentation.view_model.ToolbarFragmentViewModel
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.*
import com.studita.utils.UserUtils.observeNoNull
import com.studita.utils.UserUtils.streakActivated
import kotlinx.android.synthetic.main.profile_friend_item.view.*
import kotlinx.android.synthetic.main.profile_layout.*
import kotlinx.android.synthetic.main.profile_popup_menu_layout.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.lang.StringBuilder

open class ProfileFragment : NavigatableFragment(R.layout.profile_layout),
    SwipeRefreshLayout.OnRefreshListener, ReloadPageCallback {

    private val profileFragmentViewModel: ProfileFragmentViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
                return ProfileFragmentViewModel(PrefsUtils.getUserId()!!, userId) as T
            }
        })[ProfileFragmentViewModel::class.java]
    }

    lateinit var userData: UserDataData
    var userId = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = arguments?.getInt("USER_ID")!!

        val isMyProfile = userId == PrefsUtils.getUserId()

        profileFragmentViewModel.userDataState.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                if (it is UserDataStatus.Success) {

                    if(!isHidden)
                        initMenu()

                    fillData(it.result, view.context)

                    if (isMyProfile) {
                        if (UserUtils.userDataLiveData.value != it.result) {
                            UserUtils.userDataLiveData.value = it.result.copy()
                        }
                    }
                }
            })

        if (isMyProfile) {
            UserUtils.userDataEventsLiveData.observeNoNull(
                viewLifecycleOwner,
                androidx.lifecycle.Observer {
                    if (profileFragmentViewModel.userDataState.value != null) {
                        if (userData != it) {
                            profileFragmentViewModel.userDataState.value =
                                UserDataStatus.Success(it.copy())
                        }
                    }
                })
        }

        UserUtils.isMyFriendLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it.userData.userId == userId) {
                profileFragmentViewModel.isMyFriendState.value = it.userData.isMyFriendStatus
                formButton(it.userData, view.context)
            }

            if (it is UsersInteractor.FriendActionState.FriendshipRequestIsAccepted || it is UsersInteractor.FriendActionState.RemovedFromFriends) {
                if (isMyProfile) {
                    profileFragmentViewModel.refreshFriends(it.userData)
                } else if (it.userData.userId == userId) {
                    profileFragmentViewModel.refreshFriends(UserUtils.userData.toUserData(it.userData.isMyFriendStatus))
                }
            }

        })

        profileFragmentViewModel.isMyFriendState.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                formButton(
                    UserData(
                        userData.userId!!,
                        userData.userName!!,
                        userData.avatarLink,
                        it
                    ), view.context
                )
            })

        profileFragmentViewModel.errorEvent.observe(viewLifecycleOwner, Observer { isNetworkError->
            clearToolbarDivider()
            if (isNetworkError) {
                addFragment(InternetIsDisabledFragment(), R.id.profileLayoutFrameLayout, false)
            }else{
                addFragment(ServerProblemsFragment(), R.id.profileLayoutFrameLayout, false)
            }
        })

        profileFragmentViewModel.progressState.observe(viewLifecycleOwner, Observer {
            val progress = it

            if (profileLayoutProgressBar != null) {
                if (progress) {
                    profileLayoutProgressBar.visibility = View.VISIBLE
                    profileLayoutScrollView.visibility = View.GONE
                }else {
                    checkScroll()
                    profileLayoutProgressBar.visibility = View.GONE
                    profileLayoutScrollView.visibility = View.VISIBLE
                }
            }
        })

        profileFragmentViewModel.friendsState.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {

                if (it is GetUsersStatus.Success) {
                    fillFriends(it.friendsResponseData)
                } else if (it is GetUsersStatus.NoUsersFound) {

                    profileLayoutFriendsContentView.removeAllViews()
                    profileLayoutEmptyFriends.visibility = View.VISIBLE
                    profileLayoutFriendsMoreButton.visibility = View.GONE
                    profileLayoutFriendsTitle.text = resources.getString(R.string.friends)
                    profileLayoutEmptyFriends.visibility = View.VISIBLE
                    if (isMyProfile) {
                        profileLayoutEmptyFriendsText.text =
                            resources.getString(R.string.my_friends_empty)
                        profileLayoutEmptyFriendsButton.visibility = View.VISIBLE
                        profileLayoutEmptyFriendsButton.setOnClickListener {
                            showGlobalSearch()
                        }
                    } else {
                        profileLayoutEmptyFriendsText.text =
                            resources.getString(R.string.friends_empty)
                    }

                    (view as ViewGroup).removeView(profileLayoutProgressBar)
                    profileLayoutScrollView.visibility = View.VISIBLE

                }
            })

        profileFragmentViewModel.achievementsState.observe(
            viewLifecycleOwner
        ){
            fillAchievements(isMyProfile, it)
        }

        profileFragmentViewModel.addFriendStatus.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {

                val snackbar = CustomSnackbar(view.context)
                snackbar.show(
                    resources.getString(
                        when (it) {
                            is UsersInteractor.FriendActionState.FriendshipRequestIsAccepted -> R.string.friend_added_snackbar
                            is UsersInteractor.FriendActionState.RemovedFromFriends -> R.string.friend_removed_snackbar
                            is UsersInteractor.FriendActionState.FriendshipRequestIsCanceled -> R.string.friendship_request_is_canceled_snackbar
                            is UsersInteractor.FriendActionState.FriendshipRequestIsSent -> R.string.friendship_request_is_sent_snackbar
                            is UsersInteractor.FriendActionState.FriendshipRequestIsRejected -> R.string.friendship_request_is_rejected_snackbar
                        },
                        it.userData.userName
                    ),
                    ColorUtils.compositeColors(
                        ThemeUtils.getAccentLiteColor(snackbar.context),
                        ContextCompat.getColor(snackbar.context, R.color.white)
                    ),
                    ContextCompat.getColor(snackbar.context, R.color.black),
                    duration = resources.getInteger(R.integer.add_remove_friend_snackbar_duration)
                        .toLong()
                )
            })

        profileFragmentViewModel.errorSnackbarEvent.observe(viewLifecycleOwner, Observer {
            if(it){
                CustomSnackbar(context!!).show(
                    resources.getString(R.string.server_temporarily_unavailable),
                    ThemeUtils.getRedColor(context!!),
                    ContextCompat.getColor(context!!, R.color.white)
                )
            }
        })

        if (isMyProfile) {
            profileLayoutButton.setOnClickListener {
                (activity as AppCompatActivity).navigateTo(
                    EditProfileFragment(),
                    R.id.doubleFrameLayoutFrameLayout
                )
            }
        }
        profileLayoutFriendsMoreButton.setOnClickListener {
            navigateToFriends(isMyProfile, userId)
        }

        initRefreshLayout(view.context)
        scrollingView = profileLayoutScrollView

        profileLayoutScrollView.post {
            if (profileFragmentViewModel.errorState)
                toolbarFragmentViewModel?.hideDivider()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {

        if(!isHidden) {
            if (profileFragmentViewModel.errorState)
                toolbarFragmentViewModel?.hideDivider()
            else
                super.onHiddenChanged(hidden)
        }

        if (!isHidden)
            initMenu()

    }


    override fun onRefresh() {
        profileFragmentViewModel.getProfileData(true)
    }


    private fun fillAchievements(isMyProfile: Boolean, achievements: List<AchievementUiModel>){
        if(achievements.isNotEmpty() && (profileLayoutAchievementsScrollViewContent.childCount != achievements.size)) {
            profileLayoutAchievementsScrollView.visibility = View.VISIBLE
            profileLayoutAchievementsEmptyAchievementsTextView.visibility = View.GONE
            profileLayoutAchievementsScrollViewContent.removeAllViews()
            if (isMyProfile)
                profileLayoutAchievementsTitle.setOnClickListener {
                    (activity as AppCompatActivity).navigateTo(AchievementsFragment(), R.id.doubleFrameLayoutFrameLayout)
                }
            profileLayoutAchievementsTitle.text =
                resources.getString(R.string.achievements_count_template, achievements.size)
            profileLayoutAchievementsLayout.visibility = View.VISIBLE

            achievements.forEachIndexed { idx, uiModel ->
                val achievementView = ImageView(view!!.context).apply {
                    layoutParams = LinearLayout.LayoutParams(40F.dp, 40F.dp).apply {
                        if (idx != 0)
                            leftMargin = 8F.dp
                        if (idx != achievements.size - 1)
                            rightMargin = 8F.dp
                    }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                achievementView.loadSVG(uiModel.iconUrl, R.drawable.achievement_placeholder)
                profileLayoutAchievementsScrollViewContent.addView(
                    achievementView
                )
            }
        }else if(achievements.isEmpty()){
            profileLayoutAchievementsTitle.text =
                resources.getString(R.string.achievements)
            profileLayoutAchievementsScrollView.visibility = View.GONE
            profileLayoutAchievementsEmptyAchievementsTextView.text = resources.getString(if(isMyProfile) R.string.my_profile_empty_achievements else R.string.profile_empty_achievements)
            profileLayoutAchievementsEmptyAchievementsTextView.visibility = View.VISIBLE
        }
    }

    private fun fillData(userData: UserDataData, context: Context) {
        this.userData = userData

        profileLayoutAvatar.fillAvatar(userData.avatarLink, userData.userName!!, userData.userId!!)
        fillText(userData)
        fillStreakLevelBlock(userData, context)
        //fillCompetitionsBlock(userData)
        profileLayoutSwipeRefresh.isEnabled = true
        profileLayoutSwipeRefresh.isRefreshing = false
    }

    private fun formButton(userData: UserData, context: Context) {
        when (userData.isMyFriendStatus) {
            is IsMyFriendStatus.Success.IsMyFriend -> {
                profileLayoutButton.visibility = View.VISIBLE
                profileLayoutTransparentButton.visibility = View.GONE
                profileLayoutButton.text = resources.getString(R.string.call_to_duel)
                profileLayoutTextButton.visibility = View.VISIBLE
                profileLayoutButton.background =
                    ContextCompat.getDrawable(context, R.drawable.button_green_8)
                profileLayoutTextButton.text = resources.getString(R.string.remove_from_friends)
                profileLayoutTextButton.setOnClickListener {
                    profileFragmentViewModel.removeFriend(
                        UserUtils.getUserIDTokenData()!!,
                        userData
                    )
                }
                profileLayoutButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_emoji_events_white, 0)
                profileLayoutButton.setOnClickListener {
                    activity?.startActivity<CompetitionsActivity>()
                }
            }
            is IsMyFriendStatus.Success.IsNotMyFriend -> {
                profileLayoutButton.visibility = View.VISIBLE
                profileLayoutTransparentButton.visibility = View.GONE
                profileLayoutButton.text = resources.getString(R.string.add_to_friends)
                profileLayoutButton.background =
                    ContextCompat.getDrawable(context, R.drawable.button_accent_8)
                profileLayoutButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_person_add_white, 0)
                profileLayoutTextButton.visibility = View.GONE
                profileLayoutButton.setOnClickListener {
                    profileFragmentViewModel.sendFriendshipRequest(
                        UserUtils.getUserIDTokenData()!!,
                        userData
                    )
                }
            }
            is IsMyFriendStatus.Success.GotMyFriendshipRequest -> {
                profileLayoutButton.visibility = View.GONE
                profileLayoutTransparentButton.visibility = View.VISIBLE
                profileLayoutTextButton.visibility = View.GONE
                profileLayoutTransparentButton.setOnClickListener {
                    profileFragmentViewModel.cancelFriendshipRequest(
                        UserUtils.getUserIDTokenData()!!,
                        userData
                    )
                }
            }
            is IsMyFriendStatus.Success.WaitingForFriendshipAccept -> {
                profileLayoutButton.visibility = View.VISIBLE
                profileLayoutTransparentButton.visibility = View.GONE
                profileLayoutButton.text = resources.getString(R.string.accept_friendship_request)
                profileLayoutButton.background =
                    ContextCompat.getDrawable(context, R.drawable.button_accent_8)
                profileLayoutButton.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_done_white,
                    0
                )
                profileLayoutButton.setOnClickListener {
                    profileFragmentViewModel.acceptFriendshipRequest(
                        UserUtils.getUserIDTokenData()!!,
                        userData
                    )
                }
                profileLayoutTextButton.visibility = View.VISIBLE
                profileLayoutTextButton.text =
                    resources.getString(R.string.reject_friendship_request)
                profileLayoutTextButton.setOnClickListener {
                    profileFragmentViewModel.rejectFriendshipRequest(
                        UserUtils.getUserIDTokenData()!!,
                        userData
                    )
                }
            }
        }
    }

    private fun fillText(userDataData: UserDataData) {
        profileLayoutUserName.text = resources.getString(R.string.user_name_template, userData.userName)
        fillName(userDataData)
        fillBio(userDataData)
        adjustTextBottomMargin(userDataData)
    }

    private fun fillName(userDataData: UserDataData){
        if (userDataData.name != null) {
            profileLayoutName.text = userDataData.name
            profileLayoutName.visibility = View.VISIBLE
        } else
            profileLayoutName.visibility = View.GONE
    }

    private fun fillBio(userDataData: UserDataData){
        if (userDataData.bio != null) {
            profileLayoutBio.text = userDataData.bio
            profileLayoutBio.visibility = View.VISIBLE
        } else
            profileLayoutBio.visibility = View.GONE
    }

    private fun adjustTextBottomMargin(userDataData: UserDataData){
        when{
            userDataData.bio == null && userDataData.name != null -> {
                profileLayoutUserName.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = 8F.dp
                }
                profileLayoutName.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = 0
                }
            }
            userDataData.bio == null && userDataData.name == null -> {
                profileLayoutUserName.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = 0
                }
            }
            userDataData.bio != null && userDataData.name == null -> {
                profileLayoutUserName.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = 8F.dp
                }
            }
            else -> {
                profileLayoutUserName.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = 8F.dp
                }
                profileLayoutName.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = 14F.dp
                }
            }
        }
    }


    private fun fillStreakLevelBlock(userDataData: UserDataData, context: Context) {
        profileLayoutStreakText.text = formStreakText(userDataData, context)
        profileLayoutLevelText.text = formLevelText(userDataData, context)
    }

    private fun formLevelText(userDataData: UserDataData, context: Context): SpannableStringBuilder{
        val string = StringBuilder()
            .append(userDataData.currentLevel.toString())
            .append("\n")
            .append(resources.getString(R.string.level_small_letter))
        val indexOfLevel= string.indexOf(userDataData.currentLevel.toString())
        val levelLength = userDataData.currentLevel.toString().length

        return SpannableStringBuilder()
            .append(string.substring(0, indexOfLevel))
            .append(string.substring(indexOfLevel, indexOfLevel+levelLength).createSpannableString(
                ThemeUtils.getPrimaryColor(context),
                16F,
                ResourcesCompat.getFont(context, R.font.roboto_medium)
            ))
            .append(string.substring(indexOfLevel+levelLength))
    }


    private fun formStreakText(userDataData: UserDataData, context: Context): SpannableStringBuilder{
        val string = StringBuilder()
            .append(if(streakActivated(userDataData.streakDatetime)) "[img src=ic_whatshot_profile_activated/]" else "[img src=ic_whatshot_profile/]")
            .append(LanguageUtils.getResourcesRussianLocale(context).getQuantityString(
                R.plurals.streak_plurals_with_placeholder,
                userDataData.streakDays,
                userDataData.streakDays
            ))
        val indexOfDays = string.indexOf(userDataData.streakDays.toString())
        val daysLength = userDataData.streakDays.toString().length
        string.insert(indexOfDays+daysLength, "\n")

        return SpannableStringBuilder()
            .append(string.substring(0, indexOfDays))
            .append(string.substring(indexOfDays, indexOfDays+daysLength).createSpannableString(
                ThemeUtils.getPrimaryColor(context),
                16F,
                ResourcesCompat.getFont(context, R.font.roboto_medium)
            ))
            .append(string.substring(indexOfDays+daysLength))
    }

    private fun fillCompetitionsBlock(userDataData: UserDataData) {
        /*val isMyProfile = userDataData.userId == UserUtils.userData.userId

        profileHorizontalScrollView.setLevelRatingSubtitle(
            resources.getString(
                R.string.profile_competitions_item_rating_subtitle_template,
                resources.getString(R.string.unavailable),
                if (isMyProfile) resources.getString(R.string.You) else resources.getString(
                    R.string.user_name_template,
                    userDataData.userName
                )
            )
        )
        profileHorizontalScrollView.setXPRatingSubtitle(
            resources.getString(
                R.string.profile_competitions_item_rating_subtitle_template,
                resources.getString(R.string.unavailable),
                if (isMyProfile) resources.getString(R.string.You) else resources.getString(
                    R.string.user_name_template,
                    userDataData.userName
                )
            )
        )

        if (!isMyProfile) {
            profileHorizontalScrollView.setLevelSecondarySubtitle(
                resources.getString(
                    R.string.profile_competitions_item_secondary_subtitle_template,
                    resources.getString(R.string.unavailable),
                )
            )
            profileHorizontalScrollView.setXPSecondarySubtitle(
                resources.getString(
                    R.string.profile_competitions_item_secondary_subtitle_template,
                    resources.getString(R.string.unavailable),
                )
            )
        }*/
    }

    private fun fillFriends(friendsResponseData: UsersResponseData) {
        val friends = friendsResponseData.users

        profileLayoutFriendsTitle.text =
            resources.getString(R.string.friends_count_template, friendsResponseData.usersCount)

        val isMyProfile = userId == UserUtils.userData.userId

        profileLayoutFriendsTitle.setOnClickListener {
            navigateToFriends(isMyProfile, userId)
        }

        if (friendsResponseData.usersCount > friendsResponseData.users.size)
            profileLayoutFriendsMoreButton.visibility = View.VISIBLE
        else
            profileLayoutFriendsMoreButton.visibility = View.GONE

        profileLayoutEmptyFriends.visibility = View.GONE
        profileLayoutFriendsContentView.removeAllViews()
        friends.forEach { friendData ->
            val friendChild = profileLayoutFriendsLayout.makeView(R.layout.profile_friend_item)

            with(friendChild) {

                val isMe = friendData.userId == UserUtils.userData.userId

                if (!isMe) {
                    profileFriendItemAvatar.fillAvatar(
                        friendData.avatarLink,
                        friendData.userName,
                        friendData.userId,
                        isDarkerPlaceholder = true
                    )
                    profileFriendItemUserName.text =
                        resources.getString(R.string.user_name_template, friendData.userName)
                } else {
                    UserUtils.userDataLiveData.observeNoNull(
                        viewLifecycleOwner,
                        androidx.lifecycle.Observer {
                            profileFriendItemAvatar.fillAvatar(
                                it.avatarLink,
                                it.userName!!,
                                it.userId!!,
                                isDarkerPlaceholder = true
                            )
                            profileFriendItemUserName.text =
                                resources.getString(R.string.user_name_template, it.userName)
                        })
                }

                setOnClickListener {
                    navigateToProfile(friendData.userId, isMe)
                }
            }

            profileLayoutFriendsContentView.addView(
                friendChild,
                profileLayoutFriendsContentView.childCount
            )
        }
    }

    private fun navigateToProfile(userId: Int, isMyProfile: Boolean = false) {
        (activity as AppCompatActivity).navigateTo((if (isMyProfile) MyProfileFragment() else ProfileFragment()).apply {
            arguments = bundleOf("USER_ID" to userId)
        }, R.id.doubleFrameLayoutFrameLayout)
    }

    private fun initMenu() {
        toolbarFragmentViewModel?.setToolbarRightButtonState(
            ToolbarFragmentViewModel.ToolbarRightButtonState.IsEnabled(
                R.drawable.ic_more_vert
            ) {

                var popupWidth = 0
                PopupWindow(context).apply {
                    contentView =
                        (view as ViewGroup).makeView(R.layout.profile_popup_menu_layout) as ViewGroup
                    contentView.profilePopupMenuLayoutShareProfile.disableAllItems()
                    contentView.profilePopupMenuLayoutShowStatistics.setOnClickListener {
                        (activity as AppCompatActivity).navigateTo(
                            UserStatFragment().apply {
                                arguments = bundleOf(
                                    "USER_ID" to userId,
                                    "USER_NAME" to userData.userName,
                                    "AVATAR_LINK" to userData.avatarLink
                                )
                            }, R.id.doubleFrameLayoutFrameLayout
                        )
                        dismiss()
                    }
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    isFocusable = true
                    isOutsideTouchable = true
                    contentView.measure(0, 0)
                    popupWidth = contentView.measuredWidth
                }.showAsDropDown(
                    activity?.toolbarLayoutRightButton,
                    -popupWidth - 16F.dp,
                    (-12F).dp,
                    Gravity.END
                )
            })
    }

    private fun navigateToFriends(isMyProfile: Boolean, userId: Int) {
        (activity as AppCompatActivity).navigateTo((if (isMyProfile) MyFriendsFragment() else FriendsFragment()).apply {
            arguments = bundleOf("USER_ID" to userId)
        }, R.id.doubleFrameLayoutFrameLayout)
    }

    private fun showGlobalSearch() {
        (activity as AppCompatActivity).navigateTo(
            MyFriendsFragment()
                .apply {
            arguments =
                bundleOf("USER_ID" to UserUtils.userData.userId, "GLOBAL_SEARCH_ONLY" to true)
        }, R.id.doubleFrameLayoutFrameLayout)
    }

    private fun initRefreshLayout(context: Context) {
        profileLayoutSwipeRefresh.setProgressViewOffset(
            false,
            0,
            resources.getDimension(R.dimen.toolbarHeight).toInt() + 8F.dp
        )
        profileLayoutSwipeRefresh.setOnRefreshListener(this)
        profileLayoutSwipeRefresh.isEnabled = false
        profileLayoutSwipeRefresh.setColorSchemeColors(ContextCompat.getColor(context, R.color.black))
        profileLayoutSwipeRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(context, R.color.white)
        )
    }


    override fun onScrollChanged() {
        if(!profileFragmentViewModel.errorState)
            super.onScrollChanged()
    }

    override fun onPageReload() {
        profileFragmentViewModel.getProfileData()
        profileLayoutSwipeRefresh.isEnabled = false
    }

    private fun clearToolbarDivider(){
        toolbarFragmentViewModel?.hideDivider()
    }

}