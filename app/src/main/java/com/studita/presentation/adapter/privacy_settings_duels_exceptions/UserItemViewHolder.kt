package com.studita.presentation.adapter.privacy_settings_duels_exceptions

import android.view.View
import com.studita.R
import com.studita.presentation.model.PrivacySettingsDuelsExceptionsRecyclerUiModel
import com.studita.utils.fillAvatar
import kotlinx.android.synthetic.main.privacy_duels_exceptions_item.view.*

class UserItemViewHolder(view: View, private val exceptionSelectCallback: ExceptionSelectCallback) :
    PrivacySettingsDuelsExceptionsViewHolder<PrivacySettingsDuelsExceptionsRecyclerUiModel.UserItemUiModel>(
        view
    ) {

    lateinit var model: PrivacySettingsDuelsExceptionsRecyclerUiModel.UserItemUiModel

    override fun bind(model: PrivacySettingsDuelsExceptionsRecyclerUiModel) {
        model as PrivacySettingsDuelsExceptionsRecyclerUiModel.UserItemUiModel

        this.model = model

        with(itemView) {
            privacyDuelsExceptionsItemUserName.text =
                resources.getString(R.string.user_name_template, model.userName)
            privacyDuelsExceptionsItemCheckbox.isChecked = model.isException
            privacyDuelsExceptionsItemAvatar.fillAvatar(
                model.avatarLink,
                model.userName,
                model.userId
            )
        }

        itemView.setOnClickListener {
            model.isException = !model.isException
            exceptionSelectCallback.onExceptionSelect(
                model.userId,
                model.userName,
                model.isException
            )
            it.privacyDuelsExceptionsItemCheckbox.isChecked = model.isException
        }
    }

    interface ExceptionSelectCallback {

        fun onExceptionSelect(userId: Int, userName: String, isException: Boolean)

    }
}