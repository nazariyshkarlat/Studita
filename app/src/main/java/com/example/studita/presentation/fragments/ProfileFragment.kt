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
import com.example.studita.domain.entity.FriendsResponseData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.interactor.CheckIsMyFriendStatus
import com.example.studita.domain.interactor.GetFriendsStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.user_statistics.UserStatFragment
import com.example.studita.presentation.view_model.ProfileFragmentViewModel
import com.example.studita.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.profile_friend_item.view.*
import kotlinx.android.synthetic.main.profile_layout.*
import java.util.*

open class ProfileFragment : NavigatableFragment(R.layout.profile_layout){

    lateinit var profileFragmentViewModel: ProfileFragmentViewModel

    lateinit var userData: UserDataData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileFragmentViewModel = ViewModelProviders.of(this).get(ProfileFragmentViewModel::class.java)

        profileFragmentViewModel.friendsState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it is GetFriendsStatus.Success)
                fillFriends(it.friendsResponseData)
            else if(it is GetFriendsStatus.NoFriendsFound)
                profileLayoutFriendsLayout.visibility = View.GONE
        })

        profileFragmentViewModel.friendDataState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it is UserDataStatus.Success) {
                (view as ViewGroup).removeView(profileLayoutProgressBar)
                profileLayoutScrollView.visibility = View.VISIBLE
                userData = it.result

                profileLayoutAvatar.fillAvatar(userData.avatarLink, userData.userName!!, userData.userId!!)
                fillStreakBlock(userData, view.context)
                fillText(userData)
                fillCompetitionsBlock(userData)
            }
        })

        profileFragmentViewModel.isMyFriendState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {isMyFriendState->
            formButton(isMyFriendState, userData, view.context)
        })

            arguments?.getInt("USER_ID")?.let {
                if (savedInstanceState == null) {
                    profileFragmentViewModel.getProfileData(it, UserUtils.userData.userId!!)
                }
                val isMyProfile = it == UserUtils.userData.userId
                if (isMyProfile) {
                    profileLayoutButton.setOnClickListener {
                        (activity as AppCompatActivity).navigateTo(
                            EditProfileFragment(),
                            R.id.doubleFrameLayoutFrameLayout
                        )
                    }
                }
            }

        profileLayoutFriendsMoreButton.setOnClickListener {
            (activity as AppCompatActivity).navigateTo(FriendsFragment(), R.id.doubleFrameLayoutFrameLayout)
        }

        view.post { initMenu()}

        scrollingView = profileLayoutScrollView
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if(!isHidden) {
            initMenu()
            arguments?.getInt("USER_ID")?.let {
                profileFragmentViewModel.getProfileData(it, UserUtils.userData.userId!!)
            }
        }
    }

    private fun formButton(isMyFriendState: CheckIsMyFriendStatus, userDataData: UserDataData, context: Context){
        if(isMyFriendState is CheckIsMyFriendStatus.IsMyFriend){
            profileLayoutButton.text = resources.getString(R.string.call_to_duel)
            profileLayoutTextButton.visibility = View.VISIBLE
            profileLayoutButton.background = ContextCompat.getDrawable(context, R.drawable.button_green_8)
            profileLayoutTextButton.setOnClickListener {
                profileFragmentViewModel.removeFromFriends(UserUtils.getUserIDTokenData()!!, userDataData.userId!!)
            }
        }else if(isMyFriendState is CheckIsMyFriendStatus.IsNotMyFriend){
            profileLayoutButton.text = resources.getString(R.string.add_to_friends)
            profileLayoutButton.background = ContextCompat.getDrawable(context, R.drawable.button_accent_8)
            profileLayoutTextButton.visibility = View.GONE
            profileLayoutButton.setOnClickListener {
                profileFragmentViewModel.addToFriends(UserUtils.getUserIDTokenData()!!, userDataData.userId!!)
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

    private fun fillFriends(friendsResponseData: FriendsResponseData){
        val friends = friendsResponseData.friends

        profileLayoutFriendsTitle.text = resources.getString(R.string.friends_count_template, friendsResponseData.friendsCount)

        if(friends.isNotEmpty())
            profileLayoutFriendsLayout.visibility = View.VISIBLE

        if(friendsResponseData.friendsCount > 3)
            profileLayoutFriendsMoreButton.visibility = View.VISIBLE
        else
            profileLayoutFriendsMoreButton.visibility = View.GONE

        profileLayoutFriendsLayout.removeViews(1,profileLayoutFriendsLayout.childCount-2)
        friends.forEach {friendData->
            val friendChild = profileLayoutFriendsLayout.makeView(R.layout.profile_friend_item)

            with(friendChild){

                profileFriendItemAvatar.fillAvatar(friendData.avatarLink, friendData.friendName, friendData.friendId)
                profileFriendItemUserName.text = resources.getString(R.string.user_name_template, friendData.friendName)

                setOnClickListener {
                    val isYourProfile = friendData.friendId == UserUtils.userData.userId
                    navigateToProfile(friendData.friendId, isYourProfile)
                }
            }
            profileLayoutFriendsLayout.addView(friendChild, profileLayoutFriendsLayout.childCount-1)
        }
    }

    private fun initButton(userDataData: UserDataData, context: Context){
        val isMyProfile = userDataData.userId == UserUtils.userData.userId
        if(isMyProfile) {
            profileLayoutButton.setOnClickListener {
                if (arguments?.get("NAVIGATE_FROM_EDIT_PROFILE") == true)
                    activity?.onBackPressed()
                else
                    (activity as AppCompatActivity).navigateTo(EditProfileFragment(), R.id.doubleFrameLayoutFrameLayout)
            }
        }else{
            profileLayoutButton.background = ContextCompat.getDrawable(context, R.drawable.button_green_8)
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

    private fun streakActivated(streakDate: Date) = TimeUtils.getCalendarDayCount(Date(), streakDate) == 0L

}