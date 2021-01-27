package com.studita.presentation.adapter.levels

import android.view.View
import androidx.core.view.updatePadding
import com.studita.R
import com.studita.presentation.model.HomeRecyclerUiModel
import com.studita.utils.LanguageUtils
import com.studita.utils.dp
import kotlinx.android.synthetic.main.level_item.view.*

class LevelViewHolder(view: View, private val visibilityCallback: ChangeLevelItemsVisibilityCallback) :
    LevelsViewHolder<HomeRecyclerUiModel.HomeRecyclerLevelViewModel>(view) {

    override fun bind(model: HomeRecyclerUiModel) {
        (model as HomeRecyclerUiModel.HomeRecyclerLevelViewModel)
        with(itemView) {
            levelItemLevelName.text = model.levelName
            if (model.isExpanded) {
                levelItemButton.text = resources.getString(R.string.collapse)
                levelItemTopBlock.updatePadding(bottom = 4F.dp)
                levelItemSubtitle.visibility = View.GONE
            }else {
                levelItemButton.text = resources.getString(R.string.expand)
                levelItemTopBlock.updatePadding(bottom = 0)
                levelItemSubtitle.visibility = View.VISIBLE
                levelItemSubtitle.text = LanguageUtils.getResourcesRussianLocale(context).getQuantityString(
                    R.plurals.collapsed_exercises_count,
                    model.chaptersCount,
                    model.chaptersCount
                )
            }
            levelItemButton.setOnClickListener {
                visibilityCallback.onLevelItemsVisibilityChanged(model.levelNumber, !model.isExpanded)
            }
        }
    }

    interface ChangeLevelItemsVisibilityCallback{
        fun onLevelItemsVisibilityChanged(levelNumber: Int, isExpanded: Boolean)
    }
}