package com.example.studita.presentation.adapter.levels

import android.view.View
import com.example.studita.R
import com.example.studita.presentation.adapter.levels.LevelsViewHolder
import com.example.studita.presentation.model.HomeRecyclerUiModel
import kotlinx.android.synthetic.main.level_item.view.*

class LevelViewHolder(view: View) : LevelsViewHolder<HomeRecyclerUiModel.LevelNumber>(view){

    override fun bind(model: HomeRecyclerUiModel) {
        val modelData = (model as HomeRecyclerUiModel.LevelNumber)
        itemView.levelItemLevel.text = itemView.context.resources.getString(R.string.level_number, modelData.levelNumber)
        itemView.isActivated = modelData.levelNumber == 1
    }

}