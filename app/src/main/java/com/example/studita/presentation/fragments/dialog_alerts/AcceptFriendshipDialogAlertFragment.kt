package com.example.studita.presentation.fragments.dialog_alerts

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.data.entity.UserStatisticsEntity
import com.example.studita.domain.entity.UserData
import com.example.studita.domain.entity.serializer.IsMyFriendStatusDeserializer
import com.example.studita.domain.interactor.IsMyFriendStatus
import com.example.studita.domain.interactor.users.UsersInteractor
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import com.example.studita.presentation.view_model.FriendsFragmentViewModel
import com.example.studita.presentation.views.CustomSnackbar
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.ThemeUtils
import com.example.studita.utils.UserUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.accept_friendship_dialog_alert.*

class AcceptFriendshipDialogAlertFragment : BaseDialogFragment(R.layout.accept_friendship_dialog_alert){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val friendsFragmentViewModel = ViewModelProviders.of(this).get(FriendsFragmentViewModel::class.java)

        val json = arguments?.getString("USER_DATA")

        val userData = json?.let {
            GsonBuilder().apply {
                registerTypeAdapter(IsMyFriendStatus::class.java, IsMyFriendStatusDeserializer())
            }.create().fromJson<UserData>(json, object  : TypeToken<UserData>() {}.type)
        }

        if(userData != null){
            acceptFriendshipDialogAlertSubtitle.text = resources.getString(R.string.accept_friendship_dialog_alert_subtitle, userData.userName)

            acceptFriendshipDialogRightButton.setOnClickListener {
                friendsFragmentViewModel.addToFriends(UserUtils.getUserIDTokenData()!!, userData)
                dismiss()
            }
            acceptFriendshipDialogLeftButton.setOnClickListener {
                dismiss()
            }
        }

        val context = activity as AppCompatActivity

        friendsFragmentViewModel.addFriendStatus.observe(context, Observer {
            if(it is UsersInteractor.FriendActionState.AddedToFriends) {
                CustomSnackbar(context).show(
                    context.resources.getString(R.string.friend_added_snackbar, userData?.userName),
                    ThemeUtils.getAccentColor(context)
                )
            }
        })
    }


}