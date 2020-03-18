package com.example.studita.presentation.adapter

import android.text.TextUtils
import android.view.View
import com.example.studita.R
import com.example.studita.presentation.model.LevelUiModel
import kotlinx.android.synthetic.main.interesting_item.view.*
import kotlinx.android.synthetic.main.level_item.view.*

class InterestingViewHolder(view: View) : LevelsViewHolder<LevelUiModel.LevelInterestingUiModel>(view){

    override fun bind(model: LevelUiModel) {
        val modelData = (model as LevelUiModel.LevelInterestingUiModel)
        itemView.interestingItemTitle.text = modelData.title
        itemView.interestingItemSubtitle.text = modelData.subtitle
        fillTags(modelData)
        itemView.setOnClickListener {  }
    }

    private fun fillTags(model: LevelUiModel.LevelInterestingUiModel){
       itemView.interestingItemTagsTextView.text = model.tags.joinToString(separator =  "  #", prefix = "#")
    }

}