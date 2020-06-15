package com.example.studita.presentation.adapter.privacy_settings_duels_exceptions

import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.studita.R
import com.example.studita.presentation.fragments.MyProfileFragment
import com.example.studita.presentation.fragments.ProfileFragment
import com.example.studita.presentation.model.PrivacySettingsDuelsExceptionsRecyclerUiModel
import com.example.studita.presentation.model.UsersRecyclerUiModel
import com.example.studita.utils.*
import kotlinx.android.synthetic.main.friend_item.view.*
import kotlinx.android.synthetic.main.privacy_duels_exceptions_item.view.*

class UserItemViewHolder(view: View, private val exceptionSelectCallback: ExceptionSelectCallback) : PrivacySettingsDuelsExceptionsViewHolder<PrivacySettingsDuelsExceptionsRecyclerUiModel.UserItemUiModel>(view){

    lateinit var model: PrivacySettingsDuelsExceptionsRecyclerUiModel.UserItemUiModel

    override fun bind(model: PrivacySettingsDuelsExceptionsRecyclerUiModel) {
        model as PrivacySettingsDuelsExceptionsRecyclerUiModel.UserItemUiModel

        this.model = model

        with(itemView) {
            privacyDuelsExceptionsItemUserName.text =
                resources.getString(R.string.user_name_template, model.userName)
            privacyDuelsExceptionsItemCheckbox.isChecked = model.isException
            privacyDuelsExceptionsItemAvatar.fillAvatar(model.avatarLink, model.userName, model.userId)
        }

        itemView.setOnClickListener {
            model.isException = !model.isException
            exceptionSelectCallback.onExceptionSelect(model.userId, model.userName, model.isException)
            it.privacyDuelsExceptionsItemCheckbox.isChecked = model.isException
        }
    }

    interface ExceptionSelectCallback{

        fun onExceptionSelect(userId: Int, userName: String, isException: Boolean)

    }
}