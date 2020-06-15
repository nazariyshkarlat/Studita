package com.example.studita.presentation.adapter.privacy_settings_duels_exceptions

import android.view.View
import com.example.studita.presentation.model.PrivacySettingsDuelsExceptionsRecyclerUiModel
import com.example.studita.presentation.model.UsersRecyclerUiModel

class LoadViewHolder(view: View, private val requestMoreItems: RequestMoreItems) : PrivacySettingsDuelsExceptionsViewHolder<PrivacySettingsDuelsExceptionsRecyclerUiModel.ProgressUiModel>(view){
    override fun bind(model: PrivacySettingsDuelsExceptionsRecyclerUiModel) {
        requestMoreItems.onRequestMoreItems()
    }

    interface RequestMoreItems{

        fun onRequestMoreItems()

    }

}
