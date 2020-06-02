package com.example.studita.presentation.adapter.friends

import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.studita.R
import com.example.studita.presentation.draw.AvaDrawer
import com.example.studita.presentation.fragments.ProfileFragment
import com.example.studita.presentation.fragments.user_statistics.UserStatFragment
import com.example.studita.presentation.model.FriendsRecyclerUiModel
import com.example.studita.presentation.view_model.FriendsFragmentViewModel
import com.example.studita.utils.*
import kotlinx.android.synthetic.main.friend_item.view.*

class FriendItemViewHolder(view: View, private val friendsFragmentViewModel: FriendsFragmentViewModel) : FriendsViewHolder<FriendsRecyclerUiModel.FriendItemUiModel>(view){
    override fun bind(model: FriendsRecyclerUiModel) {
        model as FriendsRecyclerUiModel.FriendItemUiModel

        with(itemView){
            friendItemUserName.text = resources.getString(R.string.user_name_template, model.friendName)
            fillAvatar(model)
            friendItemMore.setOnClickListener {
                val wrapper = ContextThemeWrapper(it.context, R.style.PopupMenu)
                val popup = PopupMenu(wrapper, it, Gravity.END)
                popup.menuInflater.inflate(R.menu.friend_popup_menu, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item ->
                    when(item.itemId){
                        R.id.friendPopupMenuFirst -> {
                            getAppCompatActivity()?.let { it1 -> navigateToProfile(it1, model) }
                        }
                        R.id.friendPopupMenuThird -> {
                            friendsFragmentViewModel.removeFromFriends(UserUtils.getUserIDTokenData()!!, model.friendId)
                        }
                    }
                    true
                }
            }
            this.setOnClickListener {
                getAppCompatActivity()?.let { it1 -> navigateToProfile(it1, model) }
            }
        }
    }

    private fun fillAvatar(model: FriendsRecyclerUiModel.FriendItemUiModel){
        if (model.avatarLink == null) {
            AvaDrawer.drawAvatar(itemView.friendItemAvatar, model.friendName, model.friendId)
        } else {
            Glide
                    .with(itemView.context)
                    .load(model.avatarLink)
                    .centerCrop()
                    .apply(RequestOptions.circleCropTransform())
                    .into(itemView.friendItemAvatar)
        }
    }

    private fun navigateToProfile(activity: AppCompatActivity, model: FriendsRecyclerUiModel.FriendItemUiModel){
        activity.navigateTo(ProfileFragment().apply {
            arguments = bundleOf("USER_ID" to model.friendId)
        }, R.id.doubleFrameLayoutFrameLayout)
    }

}