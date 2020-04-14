package com.example.studita.presentation.adapter.levels

import android.view.View
import com.example.studita.R
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.utils.UserUtils
import kotlinx.android.synthetic.main.level_item.view.*

class LevelViewHolder(view: View) : LevelsViewHolder<HomeRecyclerUiModel.HomeRecyclerLevelViewModel>(view){

    override fun bind(model: HomeRecyclerUiModel) {
        val modelData = (model as HomeRecyclerUiModel.HomeRecyclerLevelViewModel)
        itemView.levelItemLevel.text = itemView.context.resources.getString(R.string.level_number, modelData.levelNumber)
        if(model.chapterPartsCount !=0) {
            UserUtils.userData?.completedParts?.let {
                itemView.levelItemLevel.isEnabled = model.chapterPartsCount == it.subList(
                    model.chaptersBounds.first,
                    model.chaptersBounds.second
                ).sum()
            }
        }else{
            itemView.levelItemLevel.isEnabled = false
        }
    }

}