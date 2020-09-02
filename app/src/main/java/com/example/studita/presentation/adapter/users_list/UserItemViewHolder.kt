package com.example.studita.presentation.adapter.users_list

import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.example.studita.R
import com.example.studita.domain.interactor.IsMyFriendStatus
import com.example.studita.presentation.fragments.profile.MyProfileFragment
import com.example.studita.presentation.fragments.profile.ProfileFragment
import com.example.studita.presentation.model.UsersRecyclerUiModel
import com.example.studita.utils.*
import kotlinx.android.synthetic.main.friend_item.view.*

class UserItemViewHolder(
    view: View,
    private val changeIsMyFriend: AddToFriendsCallback,
    private val lifecycleOwner: LifecycleOwner
) : UsersViewHolder<UsersRecyclerUiModel.UserItemUiModel>(view), View.OnTouchListener {

    lateinit var model: UsersRecyclerUiModel.UserItemUiModel

    override fun bind(model: UsersRecyclerUiModel) {
        model as UsersRecyclerUiModel.UserItemUiModel

        this.model = model

        val isMyProfile = model.userId == UserUtils.userData.userId

        with(itemView) {

            clearAvatar()

            if (!isMyProfile) {
                friendItemAvatar.fillAvatar(model.avatarLink, model.userName, model.userId)
                friendItemUserName.text =
                    resources.getString(R.string.user_name_template, model.userName)
            } else {
                UserUtils.userDataLiveData.observe(lifecycleOwner, androidx.lifecycle.Observer {
                    friendItemAvatar.fillAvatar(it.avatarLink, it.userName!!, it.userId!!)
                    friendItemUserName.text =
                        resources.getString(R.string.user_name_template, it.userName)
                })
            }

            friendItemAddFriend.visibility = if (isMyProfile) View.GONE else View.VISIBLE

            val isMyFriend =
                model.isMyFriendStatus is IsMyFriendStatus.Success.IsMyFriend || model.isMyFriendStatus is IsMyFriendStatus.Success.GotMyFriendshipRequest

            friendItemAddFriend.isSelected = isMyFriend

            friendItemAddFriend.setOnClickListener {
                if (isMyFriend) {
                    model.isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(model.userId)
                    it.isSelected = false
                    changeIsMyFriend.removeFriend(model.userId)
                } else {
                    model.isMyFriendStatus =
                        IsMyFriendStatus.Success.IsMyFriend(model.userId)
                    it.isSelected = true
                    changeIsMyFriend.addFriend(model.userId)
                }
            }
            this.setOnClickListener {
                this.getAppCompatActivity()?.let { it1 -> navigateToProfile(it1, model) }
            }
            this.setOnTouchListener(this@UserItemViewHolder)
        }
    }

    private fun clearAvatar() {
        Glide.with(itemView.context)
            .clear(itemView.friendItemAvatar)
    }

    interface AddToFriendsCallback {
        fun addFriend(friendId: Int)
        fun removeFriend(friendId: Int)
    }

    private fun navigateToProfile(
        activity: AppCompatActivity,
        model: UsersRecyclerUiModel.UserItemUiModel
    ) {
        val isMyProfile = model.userId == UserUtils.userData.userId
        activity.navigateTo((if (isMyProfile) MyProfileFragment() else ProfileFragment()).apply {
            arguments = bundleOf("USER_ID" to model.userId)
        }, R.id.doubleFrameLayoutFrameLayout)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == ACTION_DOWN) {
            v.getAppCompatActivity()?.hideKeyboard()
        }
        return false
    }

}