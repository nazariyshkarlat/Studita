package com.example.studita.presentation.adapter.users_list

import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.studita.R
import com.example.studita.presentation.fragments.MyProfileFragment
import com.example.studita.presentation.fragments.ProfileFragment
import com.example.studita.presentation.model.UsersRecyclerUiModel
import com.example.studita.utils.*
import kotlinx.android.synthetic.main.friend_item.view.*

class UserItemViewHolder(view: View, private val changeIsMyFriend: AddToFriendsCallback) : UsersViewHolder<UsersRecyclerUiModel.UserItemUiModel>(view), View.OnTouchListener{

    lateinit var model: UsersRecyclerUiModel.UserItemUiModel

    override fun bind(model: UsersRecyclerUiModel) {
        model as UsersRecyclerUiModel.UserItemUiModel

        this.model = model

        val isMyProfile = model.userId == UserUtils.userData.userId

        with(itemView){
            friendItemUserName.text = resources.getString(R.string.user_name_template, model.userName)

            clearAvatar()
            friendItemAvatar.fillAvatar(model.avatarLink, model.userName, model.userId)

            friendItemAddFriend.visibility =  if(isMyProfile) View.GONE else View.VISIBLE

            friendItemAddFriend.isSelected = model.isMyFriend

            friendItemAddFriend.setOnClickListener {
                if(model.isMyFriend){
                    model.isMyFriend = false
                    it.isSelected = false
                    changeIsMyFriend.removeFriend(model.userId)
                }else{
                    model.isMyFriend = true
                    it.isSelected = true
                    changeIsMyFriend.addFriend(model.userId)
                }
            }
        }
        itemView.setOnClickListener {itemView.getAppCompatActivity()?.let { it1 -> navigateToProfile(it1, model)  }}
        itemView.setOnTouchListener(this)
    }

    private fun clearAvatar(){
        Glide.with(itemView.context)
            .clear(itemView.friendItemAvatar)
    }

    interface AddToFriendsCallback{
        fun addFriend(friendId: Int)
        fun removeFriend(friendId: Int)
    }

    private fun navigateToProfile(activity: AppCompatActivity, model: UsersRecyclerUiModel.UserItemUiModel){
        val isMyProfile = model.userId == UserUtils.userData.userId
        activity.navigateTo((if(isMyProfile) MyProfileFragment() else ProfileFragment()).apply {
            arguments = bundleOf( "USER_ID" to model.userId)}, R.id.doubleFrameLayoutFrameLayout)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if(event.action == ACTION_DOWN){
            v.getAppCompatActivity()?.hideKeyboard()
        }
        return false
    }

}