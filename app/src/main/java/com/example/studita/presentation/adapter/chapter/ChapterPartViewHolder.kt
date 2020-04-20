package com.example.studita.presentation.adapter.chapter

import android.view.View
import android.view.ViewGroup
import com.example.studita.presentation.activities.ExercisesActivity
import com.example.studita.presentation.model.ChapterPartUiModel
import com.example.studita.presentation.model.ChapterUiModel
import com.example.studita.presentation.utils.UserUtils
import com.example.studita.presentation.utils.getAppCompatActivity
import com.example.studita.presentation.utils.startActivity
import kotlinx.android.synthetic.main.chapter_part_item.view.*


class ChapterPartViewHolder(view: View) : ChapterPartsViewHolder(view){

    override fun bind(model: ChapterPartUiModel, chapterUiModel: ChapterUiModel) {
        with(itemView) {
            this as ViewGroup
            chapterPartItemText.text = model.chapterPartName
            UserUtils.userData?.completedParts?.let { completedParts->
                when {
                    (model.chapterPartNumber - 1) <= completedParts[chapterUiModel.chapterNumber-1] -> {
                        if ((model.chapterPartNumber - 1) == completedParts[chapterUiModel.chapterNumber - 1]) {
                            setItemSelected()
                        }else{
                            setItemEnabled()
                        }
                        chapterPartItemButton.setOnClickListener {
                            getAppCompatActivity()?.startActivity<ExercisesActivity>("CHAPTER_NUMBER" to chapterUiModel.chapterNumber,
                                "CHAPTER_PART_NUMBER" to model.chapterPartNumber,
                                "CHAPTER_PARTS_COUNT" to chapterUiModel.parts.size,
                                "IS_TRAINING" to ((model.chapterPartNumber - 1) != completedParts[chapterUiModel.chapterNumber - 1]))
                        }
                    }
                    else -> {
                        setItemDisabled()
                    }
                }
            }
        }
    }

    private fun setItemSelected(){
        itemView.chapterPartItemIcon.isSelected = true
        itemView.chapterPartItemButton.isSelected = true
        itemView.chapterPartItemIcon.isEnabled = true
        itemView.chapterPartItemButton.isEnabled = true
    }

    private fun setItemEnabled(){
        itemView.chapterPartItemIcon.isEnabled = true
        itemView.chapterPartItemButton.isEnabled = true
        itemView.chapterPartItemIcon.isSelected = false
        itemView.chapterPartItemButton.isSelected = false
    }

    private fun setItemDisabled(){
        itemView.chapterPartItemIcon.isEnabled = false
        itemView.chapterPartItemButton.isEnabled = false
        itemView.chapterPartItemIcon.isSelected = false
        itemView.chapterPartItemButton.isSelected = false
    }

}