package com.example.studita.presentation.adapter.chapter

import android.view.View
import android.view.ViewGroup
import com.example.studita.domain.entity.ChapterData
import com.example.studita.presentation.activities.ExercisesActivity
import com.example.studita.presentation.model.ChapterPartUiModel
import com.example.studita.presentation.model.ChapterUiModel
import com.example.studita.utils.UserUtils
import com.example.studita.utils.getAppCompatActivity
import com.example.studita.utils.startActivity
import kotlinx.android.synthetic.main.chapter_part_item.view.*


class ChapterPartViewHolder(view: View) : ChapterPartsViewHolder(view) {

    override fun bind(model: ChapterPartUiModel, chapterData: ChapterData) {
        with(itemView) {
            this as ViewGroup
            chapterPartItemText.text = model.chapterPartName
            val chapterPartInChapterNumber =
                chapterData.parts.indexOfFirst { it.number == model.chapterPartNumber } + 1
            when {
                chapterPartInChapterNumber - 1 <= UserUtils.userData.completedParts[chapterData.chapterNumber - 1] -> {
                    if (isCurrentChapterPart(
                            chapterPartInChapterNumber,
                            chapterData.chapterNumber
                        )
                    ) {
                        setItemSelected()
                    } else {
                        setItemEnabled()
                    }
                    chapterPartItemButton.setOnClickListener {
                        getAppCompatActivity()?.startActivity<ExercisesActivity>(
                            "CHAPTER_NUMBER" to chapterData.chapterNumber,
                            "CHAPTER_PART_NUMBER" to model.chapterPartNumber,
                            "CHAPTER_PARTS_IN_CHAPTER_COUNT" to chapterData.parts.size,
                            "CHAPTER_NAME" to chapterData.title,
                            "EXERCISES_IN_CHAPTER_COUNT" to chapterData.exercisesCount,
                            "CHAPTER_PART_IN_CHAPTER_NUMBER" to chapterPartInChapterNumber,
                            "IS_TRAINING" to (chapterPartInChapterNumber - 1 != UserUtils.userData.completedParts[chapterData.chapterNumber - 1])
                        )
                    }
                }
                else -> {
                    setItemDisabled()
                }
            }
        }
    }

    private fun setItemSelected() {
        itemView.chapterPartItemIcon.isSelected = true
        itemView.chapterPartItemButton.isSelected = true
        itemView.chapterPartItemIcon.isEnabled = true
        itemView.chapterPartItemButton.isEnabled = true
    }

    private fun setItemEnabled() {
        itemView.chapterPartItemIcon.isEnabled = true
        itemView.chapterPartItemButton.isEnabled = true
        itemView.chapterPartItemIcon.isSelected = false
        itemView.chapterPartItemButton.isSelected = false
    }

    private fun setItemDisabled() {
        itemView.chapterPartItemIcon.isEnabled = false
        itemView.chapterPartItemButton.isEnabled = false
        itemView.chapterPartItemIcon.isSelected = false
        itemView.chapterPartItemButton.isSelected = false
    }

    private fun isCurrentChapterPart(chapterPartInChapterNumber: Int, chapterNumber: Int) =
        (chapterPartInChapterNumber - 1) == UserUtils.userData.completedParts[chapterNumber - 1]

}