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
import com.example.studita.R
import com.example.studita.domain.entity.UserData
import com.example.studita.domain.entity.UsersResponseData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.toUserData
import com.example.studita.domain.interactor.IsMyFriendStatus
import com.example.studita.domain.interactor.GetUsersStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.domain.interactor.users.UsersInteractor
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.user_statistics.UserStatFragment
import com.example.studita.presentation.view_model.ProfileFragmentViewModel
import com.example.studita.presentation.views.CustomSnackbar
import com.example.studita.utils.*
import kotlinx.android.synthetic.main.profile_friend_item.view.*
import kotlinx.android.synthetic.main.profile_layout.*
import java.util.*

open class ProfileFragment : NavigatableFragment(R.layout.profile_layout){

    private lateinit var profileFragmentViewModel: ProfileFragmentViewModel

    lateinit var userData: UserDataData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileFragmentViewModel = ViewModelProviders.of(this).get(ProfileFragmentViewModel::class.java)


        val isMyProfile = arguments?.getInt("USER_ID") == PrefsUtils.getUserId()
        if(!isMyProfile){
            profileFragmentViewModel.friendDataState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if(it is UserDataStatus.Success) {
                    userData = it.result

                    profileLayoutAvatar.fillAvatar(userData.avatarLink, userData.userName!!, userData.userId!!)
                    fillText(userData)
                    fillStreakBlock(userData, view.context)
                    fillCompetitionsBlock(userData)
                }
            })}else {
            UserUtils.userDataLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                userData = it

                profileLayoutAvatar.fillAvatar(
                    userData.avatarLink,
                    userData.userName!!,
                    userData.userId!!
                )

                fillText(userData)
                fillStreakBlock(userData, view.context)
                fillCompetitionsBlock(userData)
            })
        }

        UserUtils.isMyFriendLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {userData->

            if (userData.userId == arguments?.getInt("USER_ID")) {
                profileFragmentViewModel.isMyFriendState.value = userData.isMyFriendStatus
                formButton(userData, view.context)
            }

            val changeUserHasMeInFriends = userData.userId == arguments?.getInt("USER_ID")

            if(isMyProfile){
                profileFragmentViewModel.refreshFriends(userData)
            }else if(changeUserHasMeInFriends){
                profileFragmentViewModel.refreshFriends(this.userData.toUserData(userData.isMyFriendStatus))
            }

        })

        profileFragmentViewModel.isMyFriendState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            formButton(UserData(userData.userId!!, userData.userName!!, userData.avatarLink, it), view.context)
        })

        profileFragmentViewModel.friendsState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if(it is GetUsersStatus.Success) {
                fillFriends(it.friendsResponseData)

                if(profileLayoutProgressBar != null) {
                    (view as ViewGroup).removeView(profileLayoutProgressBar)
                    profileLayoutScrollView.visibility = View.VISIBLE
                }

            }else if(it is GetUsersStatus.NoUsersFound) {

                profileLayoutFriendsContentView.removeAllViews()
                profileLayoutEmptyFriends.visibility = View.VISIBLE
                profileLayoutFriendsMoreButton.visibility = View.GONE
                profileLayoutFriendsTitle.text = resources.getString(R.string.friends)
                profileLayoutEmptyFriends.visibility = View.VISIBLE
                if(isMyProfile) {
                    profileLayoutEmptyFriendsText.text = resources.getString(R.string.my_friends_empty)
                    profileLayoutEmptyFriendsButton.visibility = View.VISIBLE
                    profileLayoutEmptyFriendsButton.setOnClickListener {
                        showGlobalSearch()
                    }
                }else{
                    profileLayoutEmptyFriendsText.text = resources.getString(R.string.friends_empty)
                }

                (view as ViewGroup).removeView(profileLayoutProgressBar)
                profileLayoutScrollView.visibility = View.VISIBLE

            }
        })

        profileFragmentViewModel.addFriendStatus.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            val snackbar = CustomSnackbar(view.context)
            snackbar.show(
                resources.getString(
                    when(it) {
                        is UsersInteractor.FriendActionState.AddedToFriends -> R.string.friend_added_snackbar
                        is UsersInteractor.FriendActionState.RemovedFromFriends -> R.string.friend_removed_snackbar
                        is UsersInteractor.FriendActionState.MyFriendshipRequestIsCanceled -> R.string.friendship_request_is_canceled_snackbar
                        is UsersInteractor.FriendActionState.FriendshipRequestIsSent -> R.string.friendship_request_is_sent_snackbar
                    },
                    it.userData.userName), ThemeUtils.getAccentColor(snackbar.context), duration = resources.getInteger(R.integer.add_remove_friend_snackbar_duration).toLong())
        })

        arguments?.getInt("USER_ID")?.let {userId->
            if (savedInstanceState == null) {
                profileFragmentViewModel.getProfileData(PrefsUtils.getUserId()!!, userId)
            }
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
        }


        if(isVisible)
            view.post { initMenu()}

        scrollingView = profileLayoutScrollView
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if(isVisible)
            initMenu()
    }


    private fun formButton(userData: UserData, context: Context){
        when (userData.isMyFriendStatus) {
            is IsMyFriendStatus.Success.IsMyFriend -> {
                profileLayoutButton.visibility = View.VISIBLE
                profileLayoutTransparentButton.visibility = View.GONE
                profileLayoutButton.text = resources.getString(R.string.call_to_duel)
                profileLayoutTextButton.visibility = View.VISIBLE
                profileLayoutButton.background = ContextCompat.getDrawable(context, R.drawable.button_green_8)
                profileLayoutTextButton.setOnClickListener {
                    profileFragmentViewModel.removeFromFriends(UserUtils.getUserIDTokenData()!!, userData)
                }
            }
            is IsMyFriendStatus.Success.IsNotMyFriend -> {
                profileLayoutButton.visibility = View.VISIBLE
                profileLayoutTransparentButton.visibility = View.GONE
                profileLayoutButton.text = resources.getString(R.string.add_to_friends)
                profileLayoutButton.background = ContextCompat.getDrawable(context, R.drawable.button_accent_8)
                profileLayoutTextButton.visibility = View.GONE
                profileLayoutButton.setOnClickListener {
                    profileFragmentViewModel.addToFriends(UserUtils.getUserIDTokenData()!!, userData)
                }
            }
            is IsMyFriendStatus.Success.GotMyFriendshipRequest -> {
                profileLayoutButton.visibility = View.GONE
                profileLayoutTransparentButton.visibility = View.VISIBLE
                profileLayoutTransparentButton.setOnClickListener {
                    profileFragmentViewModel.cancelFriendshipRequest(UserUtils.getUserIDTokenData()!!, userData)
                }
            }
            is IsMyFriendStatus.Success.WaitingForFriendshipAccept ->{
                profileLayoutButton.visibility = View.VISIBLE
                profileLayoutTransparentButton.visibility = View.GONE
                profileLayoutButton.text = resources.getString(R.string.accept_friendship_request)
                profileLayoutButton.background = ContextCompat.getDrawable(context, R.drawable.button_accent_8)
                profileLayoutTextButton.visibility = View.GONE
                profileLayoutButton.setOnClickListener {
                    profileFragmentViewModel.acceptFriendshipRequest(UserUtils.getUserIDTokenData()!!, userData)
                }
            }
        }
    }

    private fun fillText(userDataData: UserDataData){
        profileLayoutUserName.text = resources.getString(R.string.user_name_template, userDataData.userName)
        if(userDataData.userFullName != null) {
            profileLayoutUserFullName.text = userDataData.userFullName
            profileLayoutUserFullName.visibility = View.VISIBLE
        }else
            profileLayoutUserFullName.visibility = View.GONE
        profileLayoutLevelOval.text = userDataData.currentLevel.toString()
    }

    private fun fillStreakBlock(userDataData: UserDataData, context: Context){
        profileLayoutStreakText.text =
                LanguageUtils.getResourcesRussianLocale(context)?.getQuantityString(R.plurals.streak_plurals, userDataData.streakDays, userDataData.streakDays)
        profileLayoutStreakIcon.isActivated = streakActivated(userDataData.streakDatetime)
    }

    private fun fillCompetitionsBlock(userDataData: UserDataData){
        val isMyProfile = userDataData.userId == UserUtils.userData.userId

        profileHorizontalScrollView.setLevelRatingSubtitle(resources.getString(R.string.profile_competitions_item_rating_subtitle_template, userDataData.currentLevel, if(isMyProfile) resources.getString( R.string.You) else resources.getString(R.string.user_name_template, userDataData.userName)))
        profileHorizontalScrollView.setXPRatingSubtitle(resources.getString(R.string.profile_competitions_item_rating_subtitle_template, userDataData.currentLevel, if(isMyProfile) resources.getString( R.string.You) else resources.getString(R.string.user_name_template, userDataData.userName)))

        if(!isMyProfile){
            profileHorizontalScrollView.setLevelSecondarySubtitle(resources.getString(R.string.profile_competitions_item_secondary_subtitle_template, userDataData.currentLevel))
            profileHorizontalScrollView.setXPSecondarySubtitle(resources.getString(R.string.profile_competitions_item_secondary_subtitle_template, userDataData.currentLevel))
        }
    }

    private fun fillFriends(friendsResponseData: UsersResponseData){
        val friends = friendsResponseData.users

        profileLayoutFriendsTitle.text = resources.getString(R.string.friends_count_template, friendsResponseData.usersCount)

        val isMyProfile = arguments?.getInt("USER_ID") == UserUtils.userData.userId

        profileLayoutFriendsTitle.setOnClickListener {
            navigateToFriends(isMyProfile, arguments?.getInt("USER_ID")!!)
        }

        if(friendsResponseData.usersCount > friendsResponseData.users.size)
            profileLayoutFriendsMoreButton.visibility = View.VISIBLE
        else
            profileLayoutFriendsMoreButton.visibility = View.GONE

        profileLayoutEmptyFriends.visibility = View.GONE
        profileLayoutFriendsContentView.removeAllViews()
        friends.forEach {friendData->
            val friendChild = profileLayoutFriendsLayout.makeView(R.layout.profile_friend_item)

            with(friendChild){

                profileFriendItemAvatar.fillAvatar(friendData.avatarLink, friendData.userName, friendData.userId)
                profileFriendItemUserName.text = resources.getString(R.string.user_name_template, friendData.userName)

                setOnClickListener {
                    val isI = friendData.userId == UserUtils.userData.userId
                    navigateToProfile(friendData.userId, isI)
                }
            }

            profileLayoutFriendsContentView.addView(friendChild, profileLayoutFriendsContentView.childCount)
        }
    }

    private fun navigateToProfile(userId: Int, isMyProfile: Boolean = false){
        (activity as AppCompatActivity).navigateTo((if(isMyProfile) MyProfileFragment() else ProfileFragment()).apply {
            arguments = bundleOf( "USER_ID" to userId)}, R.id.doubleFrameLayoutFrameLayout)
    }

    private fun initMenu(){
        toolbarFragmentViewModel?.showRightButtonAndSetOnClick(R.drawable.ic_more_vert){
            val wrapper = ContextThemeWrapper(it.context, R.style.PopupMenu)
            val popup = PopupMenu(wrapper, it, Gravity.END)
            popup.menuInflater.inflate(R.menu.profile_popup_menu, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.profilePopupMenuSecond -> (activity as AppCompatActivity).navigateTo(
                        UserStatFragment().apply {
                            arguments = bundleOf("USER_ID" to userData.userId)
                        }, R.id.doubleFrameLayoutFrameLayout)
                }
                true
            }
        }
    }

    private fun navigateToFriends(isMyProfile: Boolean, userId: Int){
        (activity as AppCompatActivity).navigateTo((if(isMyProfile) MyFriendsFragment() else FriendsFragment()).apply {
            arguments = bundleOf("USER_ID" to userId)
        }, R.id.doubleFrameLayoutFrameLayout)
    }

    private fun showGlobalSearch(){
        (activity as AppCompatActivity).navigateTo(MyFriendsFragment().apply {
            arguments = bundleOf("USER_ID" to UserUtils.userData.userId, "GLOBAL_SEARCH_ONLY" to true)
        }, R.id.doubleFrameLayoutFrameLayout)
    }

    private fun streakActivated(streakDate: Date) = TimeUtils.getCalendarDayCount(Date(), streakDate) == 0L

}