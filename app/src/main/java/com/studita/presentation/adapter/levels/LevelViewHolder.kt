package com.studita.presentation.adapter.levels

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.studita.R
import com.studita.presentation.model.HomeRecyclerUiModel
import com.studita.utils.UserUtils
import com.studita.utils.UserUtils.observeNoNull
import kotlinx.android.synthetic.main.level_item.view.*

class LevelViewHolder(view: View, private val lifecycleOwner: LifecycleOwner) :
    LevelsViewHolder<HomeRecyclerUiModel.HomeRecyclerLevelViewModel>(view) {

    override fun bind(model: HomeRecyclerUiModel) {
        val modelData = (model as HomeRecyclerUiModel.HomeRecyclerLevelViewModel)
        itemView.levelItemLevel.text =
            itemView.context.resources.getString(R.string.level_number, modelData.levelNumber)
        if (model.chapterPartsCount != 0) {
            UserUtils.userDataLiveData.observeNoNull(lifecycleOwner, Observer {
                itemView.levelItemLevel.isEnabled =
                    model.chapterPartsCount == it.completedParts.subList(
                        model.chaptersBounds.first,
                        model.chaptersBounds.second+1
                    ).sum()
            })
        } else {
            itemView.levelItemLevel.isEnabled = false
        }
    }

}