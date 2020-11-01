package com.example.studita.presentation.adapter.privacy_settings_duels_exceptions

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.domain.entity.EditDuelsExceptionsData
import com.example.studita.presentation.model.NotificationsUiModel
import com.example.studita.presentation.model.PrivacySettingsDuelsExceptionsRecyclerUiModel
import com.example.studita.presentation.view_model.PrivacySettingsDuelsExceptionsViewModel
import com.example.studita.utils.UserUtils
import com.example.studita.utils.makeView

class PrivacySettingsDuelsExceptionsAdapter(
    var items: ArrayList<PrivacySettingsDuelsExceptionsRecyclerUiModel>,
    private val privacySettingsDuelsExceptionsViewModel: PrivacySettingsDuelsExceptionsViewModel
) :
    RecyclerView.Adapter<PrivacySettingsDuelsExceptionsViewHolder<*>>(),
    UserItemViewHolder.ExceptionSelectCallback {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrivacySettingsDuelsExceptionsViewHolder<out PrivacySettingsDuelsExceptionsRecyclerUiModel> {
        return when (viewType) {
            ViewType.ITEM.ordinal -> UserItemViewHolder(
                parent.makeView(R.layout.privacy_duels_exceptions_item),
                this
            )
            ViewType.ITEMS_LOAD.ordinal -> LoadViewHolder(
                parent.makeView(R.layout.list_load_item)
            )
            else -> throw UnsupportedOperationException("unknown type of item")
        }
    }

    override fun onBindViewHolder(
        holder: PrivacySettingsDuelsExceptionsViewHolder<out PrivacySettingsDuelsExceptionsRecyclerUiModel>,
        position: Int
    ) {
        if ((position % privacySettingsDuelsExceptionsViewModel.perPage == privacySettingsDuelsExceptionsViewModel.perPage / 2) &&
            (position+privacySettingsDuelsExceptionsViewModel.perPage > itemCount) &&
            items.any { it is PrivacySettingsDuelsExceptionsRecyclerUiModel.ProgressUiModel} )
            requestMoreItems()
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int) =
        when (items[position]) {
            is PrivacySettingsDuelsExceptionsRecyclerUiModel.UserItemUiModel -> ViewType.ITEM.ordinal
            is PrivacySettingsDuelsExceptionsRecyclerUiModel.ProgressUiModel -> ViewType.ITEMS_LOAD.ordinal
        }

    override fun getItemCount() = items.size

    private fun requestMoreItems() {
        privacySettingsDuelsExceptionsViewModel.getPrivacySettingsDuelsExceptions(
            UserUtils.getUserIDTokenData()!!,
            true
        )
    }

    override fun onExceptionSelect(userId: Int, userName: String, isException: Boolean) {
        privacySettingsDuelsExceptionsViewModel.editedDuelsExceptionsData.removeAll { it.exceptionId == userId }
        privacySettingsDuelsExceptionsViewModel.editedDuelsExceptionsData.add(
            EditDuelsExceptionsData(!isException, userId, userName)
        )
    }

}


abstract class PrivacySettingsDuelsExceptionsViewHolder<T : PrivacySettingsDuelsExceptionsRecyclerUiModel>(
    view: View
) : RecyclerView.ViewHolder(view) {

    abstract fun bind(model: PrivacySettingsDuelsExceptionsRecyclerUiModel)
}

enum class ViewType {
    ITEM,
    ITEMS_LOAD
}