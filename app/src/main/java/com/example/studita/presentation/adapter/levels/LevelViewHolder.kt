package com.example.studita.presentation.adapter

import android.view.View
import com.example.studita.R
import com.example.studita.presentation.adapter.levels.LevelsViewHolder
import com.example.studita.presentation.model.LevelUiModel
import kotlinx.android.synthetic.main.level_item.view.*

class LevelViewHolder(view: View) : LevelsViewHolder<LevelUiModel.LevelNumber>(view){

    override fun bind(model: LevelUiModel?) {
        val modelData = (model as LevelUiModel.LevelNumber)
        itemView.levelItemLevel.text = itemView.context.resources.getString(R.string.level_number, modelData.levelNumber)
        itemView.isActivated = modelData.levelNumber == 1
    }

}