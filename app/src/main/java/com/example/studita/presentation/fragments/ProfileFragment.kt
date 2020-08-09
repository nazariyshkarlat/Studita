package com.example.studita.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.studita.R
import com.example.studita.domain.entity.UserData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UsersResponseData
import com.example.studita.domain.entity.toUserData
import com.example.studita.domain.interactor.GetUsersStatus
import com.example.studita.domain.interactor.IsMyFriendStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.domain.interactor.users.UsersInteractor
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.user_statistics.UserStatFragment
import com.example.studita.presentation.view_model.ProfileFragmentViewModel
import com.example.studita.presentation.view_model.ToolbarFragmentViewModel
import com.example.studita.presentation.views.CustomSnackbar
import com.example.studita.utils.*
import kotlinx.android.synthetic.main.profile_friend_item.view.*
import kotlinx.android.synthetic.main.profile_layout.*
import java.util.*

open class ProfileFragment : NavigatableFragment(R.layout.profile_layout),
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var profileFragmentViewModel: ProfileFragmentViewModel

    lateinit var userData: UserDataData
    var userId = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileFragmentViewModel =
            ViewModelProviders.of(this).get(ProfileFragmentViewModel::class.java)

        userId = arguments?.getInt("USER_ID")!!

        if (savedInstanceState == null)
            profileFragmentViewModel.getProfileData(PrefsUtils.getUserId()!!, userId)

        val isMyProfile = userId == PrefsUtils.getUserId()

        profileFragmentViewModel.userDataState.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                if (it is UserDataStatus.Success) {

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
            UserUtils.userDataEventsLiveData.observe(
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

        profileFragmentViewModel.friendsState.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {

                if (it is GetUsersStatus.Success) {
                    fillFriends(it.friendsResponseData)

                    if (profileLayoutProgressBar != null) {
                        (view as ViewGroup).removeView(profileLayoutProgressBar)
                        profileLayoutScrollView.visibility = View.VISIBLE
                    }

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
                    ThemeUtils.getAccentColor(snackbar.context),
                    duration = resources.getInteger(R.integer.add_remove_friend_snackbar_duration)
                        .toLong()
                )
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
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!isHidden)
            initMenu()
    }


    override fun onRefresh() {
        profileFragmentViewModel.getProfileData(PrefsUtils.getUserId()!!, userId)
    }

    private fun fillData(userData: UserDataData, context: Context) {
        this.userData = userData

        profileLayoutAvatar.fillAvatar(userData.avatarLink, userData.userName!!, userData.userId!!)
        fillText(userData)
        fillStreakBlock(userData, context)
        fillCompetitionsBlock(userData)
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
                profileLayoutTextButton.setOnClickListener {
                    profileFragmentViewModel.removeFriend(
                        UserUtils.getUserIDTokenData()!!,
                        userData
                    )
                }
                profileLayoutButton.setOnClickListener {}
            }
            is IsMyFriendStatus.Success.IsNotMyFriend -> {
                profileLayoutButton.visibility = View.VISIBLE
                profileLayoutTransparentButton.visibility = View.GONE
                profileLayoutButton.text = resources.getString(R.string.add_to_friends)
                profileLayoutButton.background =
                    ContextCompat.getDrawable(context, R.drawable.button_accent_8)
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
                profileLayoutTextButton.visibility = View.GONE
                profileLayoutButton.setOnClickListener {
                    profileFragmentViewModel.acceptFriendshipRequest(
                        UserUtils.getUserIDTokenData()!!,
                        userData
                    )
                }
            }
        }
    }

    private fun fillText(userDataData: UserDataData) {
        profileLayoutUserName.text =
            resources.getString(R.string.user_name_template, userDataData.userName)
        if (userDataData.name != null) {
            profileLayoutName.text = userDataData.name
            profileLayoutName.visibility = View.VISIBLE
        } else
            profileLayoutName.visibility = View.GONE
        profileLayoutLevelOval.text = userDataData.currentLevel.toString()
    }

    private fun fillStreakBlock(userDataData: UserDataData, context: Context) {
        profileLayoutStreakText.text =
            LanguageUtils.getResourcesRussianLocale(context)?.getQuantityString(
                R.plurals.streak_plurals,
                userDataData.streakDays,
                userDataData.streakDays
            )
        profileLayoutStreakIcon.isActivated = streakActivated(userDataData.streakDatetime)
    }

    private fun fillCompetitionsBlock(userDataData: UserDataData) {
        val isMyProfile = userDataData.userId == UserUtils.userData.userId

        profileHorizontalScrollView.setLevelRatingSubtitle(
            resources.getString(
                R.string.profile_competitions_item_rating_subtitle_template,
                UserUtils.userData.currentLevel + 1,
                if (isMyProfile) resources.getString(R.string.You) else resources.getString(
                    R.string.user_name_template,
                    userDataData.userName
                )
            )
        )
        profileHorizontalScrollView.setXPRatingSubtitle(
            resources.getString(
                R.string.profile_competitions_item_rating_subtitle_template,
                userDataData.currentLevel,
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
                    UserUtils.userData.currentLevel + 1
                )
            )
            profileHorizontalScrollView.setXPSecondarySubtitle(
                resources.getString(
                    R.string.profile_competitions_item_secondary_subtitle_template,
                    userDataData.currentLevel
                )
            )
        }
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
                        friendData.userId
                    )
                    profileFriendItemUserName.text =
                        resources.getString(R.string.user_name_template, friendData.userName)
                } else {
                    UserUtils.userDataLiveData.observe(
                        viewLifecycleOwner,
                        androidx.lifecycle.Observer {
                            profileFriendItemAvatar.fillAvatar(
                                it.avatarLink,
                                it.userName!!,
                                it.userId!!
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
                val wrapper = ContextThemeWrapper(it.context, R.style.PopupMenu)
                val popup = PopupMenu(wrapper, it, Gravity.END)
                popup.menuInflater.inflate(R.menu.profile_popup_menu, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.profilePopupMenuSecond -> (activity as AppCompatActivity).navigateTo(
                            UserStatFragment().apply {
                                arguments = bundleOf(
                                    "USER_ID" to userId,
                                    "USER_NAME" to userData.userName,
                                    "AVATAR_LINK" to userData.avatarLink
                                )
                            }, R.id.doubleFrameLayoutFrameLayout
                        )
                    }
                    true
                }
            })
    }

    private fun navigateToFriends(isMyProfile: Boolean, userId: Int) {
        (activity as AppCompatActivity).navigateTo((if (isMyProfile) MyFriendsFragment() else FriendsFragment()).apply {
            arguments = bundleOf("USER_ID" to userId)
        }, R.id.doubleFrameLayoutFrameLayout)
    }

    private fun showGlobalSearch() {
        (activity as AppCompatActivity).navigateTo(MyFriendsFragment().apply {
            arguments =
                bundleOf("USER_ID" to UserUtils.userData.userId, "GLOBAL_SEARCH_ONLY" to true)
        }, R.id.doubleFrameLayoutFrameLayout)
    }

    private fun initRefreshLayout(context: Context) {
        profileLayoutSwipeRefresh.setProgressViewOffset(
            false,
            0,
            resources.getDimension(R.dimen.toolbarHeight).toInt()
        )
        profileLayoutSwipeRefresh.setOnRefreshListener(this)
        profileLayoutSwipeRefresh.isEnabled = false
        profileLayoutSwipeRefresh.setColorSchemeColors(ThemeUtils.getAccentColor(context))
        profileLayoutSwipeRefresh.setProgressBackgroundColorSchemeColor(
            ThemeUtils.getSwipeRefreshBackgroundColor(
                context
            )
        )
    }

    private fun streakActivated(streakDate: Date) =
        TimeUtils.getCalendarDayCount(Date(), streakDate) == 0L

}