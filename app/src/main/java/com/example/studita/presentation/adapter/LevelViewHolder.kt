package com.example.studita.presentation.adapter

import android.view.View
import com.example.studita.R
import com.example.studita.presentation.model.LevelUiModel
import kotlinx.android.synthetic.main.level_layout.view.*

class LevelViewHolder(view: View) : LevelsViewHolder<LevelUiModel.LevelNumber>(view){

    override fun bind(model: Any) {
        val modelData = (model as LevelUiModel.LevelNumber)
        itemView.levelLayoutLevel.text = itemView.context.resources.getString(R.string.level_number, modelData.value)
        if(layoutPosition == 1)
            itemView.isActivated = true
    }

}