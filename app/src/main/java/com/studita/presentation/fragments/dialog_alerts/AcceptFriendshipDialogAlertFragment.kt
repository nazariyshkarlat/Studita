package com.studita.presentation.fragments.dialog_alerts

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.domain.entity.UserData
import com.studita.domain.entity.serializer.IsMyFriendStatusDeserializer
import com.studita.domain.interactor.IsMyFriendStatus
import com.studita.domain.interactor.users.UsersInteractor
import com.studita.presentation.fragments.base.BaseDialogFragment
import com.studita.presentation.view_model.AcceptFriendshipDialogAlertViewModel
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.ThemeUtils
import com.studita.utils.UserUtils
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.dialog_alert_layout.*

class AcceptFriendshipDialogAlertFragment :
    BaseDialogFragment(R.layout.dialog_alert_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setText(resources.getString(R.string.accept_friendship_dialog_alert_title),
            resources.getString(R.string.accept_friendship_dialog_alert_subtitle),
            resources.getString(R.string.reject),
            resources.getString(R.string.add)
        )

        val acceptFriendshipDialogAlertViewModel =
            ViewModelProviders.of(this).get(AcceptFriendshipDialogAlertViewModel::class.java)

        val json = arguments?.getString("USER_DATA")

        val userData = json?.let {
            GsonBuilder().apply {
                registerTypeAdapter(
                    IsMyFriendStatus.Success::class.java,
                    IsMyFriendStatusDeserializer()
                )
            }.create().fromJson<UserData>(json, object : TypeToken<UserData>() {}.type)
        }

        if (userData != null) {
            dialogAlertSubtitle.text = resources.getString(
                R.string.accept_friendship_dialog_alert_subtitle,
                userData.userName
            )

            dialogAlertRightButton.setOnClickListener {
                acceptFriendshipDialogAlertViewModel.acceptFriendshipRequest(
                    UserUtils.getUserIDTokenData()!!,
                    userData
                )
                dismiss()
            }
            dialogAlertLeftButton.setOnClickListener {
                acceptFriendshipDialogAlertViewModel.rejectFriendshipRequest(
                    UserUtils.getUserIDTokenData()!!,
                    userData
                )
                dismiss()
            }
        }

        val context = activity as AppCompatActivity

        acceptFriendshipDialogAlertViewModel.addFriendStatus.observe(context, Observer {
            when (it) {
                is UsersInteractor.FriendActionState.FriendshipRequestIsAccepted -> {
                    CustomSnackbar(context).show(
                        context.resources.getString(
                            R.string.friend_added_snackbar,
                            userData?.userName
                        ),
                        ColorUtils.compositeColors(
                            ThemeUtils.getAccentLiteColor(context),
                            ContextCompat.getColor(context, R.color.white)
                        ),
                        ContextCompat.getColor(context, R.color.black)
                        )
                }
                is UsersInteractor.FriendActionState.FriendshipRequestIsRejected -> {
                    CustomSnackbar(context).show(
                        context.resources.getString(
                            R.string.friendship_request_is_rejected_snackbar,
                            userData?.userName
                        ),
                        ColorUtils.compositeColors(
                            ThemeUtils.getAccentLiteColor(context),
                            ContextCompat.getColor(context, R.color.white)
                        ),
                        ContextCompat.getColor(context, R.color.black)
                    )
                }
            }
        })

        acceptFriendshipDialogAlertViewModel.errorEvent.observe(context, Observer {
            CustomSnackbar(context).show(
                context.resources.getString(R.string.server_temporarily_unavailable),
                ThemeUtils.getRedColor(context),
                ContextCompat.getColor(context, R.color.white)
            )
        })
    }


}