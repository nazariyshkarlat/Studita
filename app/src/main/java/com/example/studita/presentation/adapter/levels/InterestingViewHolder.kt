package com.example.studita.presentation.adapter.levels

import android.view.View
import com.example.studita.presentation.adapter.levels.LevelsViewHolder
import com.example.studita.presentation.model.HomeRecyclerUiModel
import kotlinx.android.synthetic.main.interesting_item.view.*

class InterestingViewHolder(view: View) : LevelsViewHolder<HomeRecyclerUiModel.LevelInterestingUiModel>(view){

    override fun bind(model: HomeRecyclerUiModel) {
        val modelData = (model as HomeRecyclerUiModel.LevelInterestingUiModel)
        itemView.interestingItemTitle.text = modelData.title
        itemView.interestingItemSubtitle.text = modelData.subtitle
        fillTags(modelData)
        itemView.setOnClickListener {  }
    }

    private fun fillTags(model: HomeRecyclerUiModel.LevelInterestingUiModel){
       itemView.interestingItemTagsTextView.text = model.tags.joinToString(separator =  "  #", prefix = "#")
    }

}