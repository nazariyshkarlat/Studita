package com.studita.presentation.adapter.levels

import android.view.View
import com.studita.presentation.activities.InterestingActivity
import com.studita.presentation.model.HomeRecyclerUiModel
import com.studita.utils.getAppCompatActivity
import com.studita.utils.startActivity
import kotlinx.android.synthetic.main.interesting_item.view.*

class InterestingViewHolder(view: View) :
    LevelsViewHolder<HomeRecyclerUiModel.LevelInterestingUiModel>(view) {

    override fun bind(model: HomeRecyclerUiModel) {
        model as HomeRecyclerUiModel.LevelInterestingUiModel
        itemView.interestingItemTitle.text = model.title
        itemView.interestingItemSubtitle.text = model.subtitle
        fillTags(model)

        itemView.interestingItemParentLayout.setOnClickListener {
            it.getAppCompatActivity()
                ?.startActivity<InterestingActivity>("INTERESTING_NUMBER" to model.interestingNumber)
        }
    }

    private fun fillTags(model: HomeRecyclerUiModel.LevelInterestingUiModel) {
        itemView.interestingItemTagsTextView.text =
            model.tags.joinToString(separator = "  #", prefix = "#")
    }

}