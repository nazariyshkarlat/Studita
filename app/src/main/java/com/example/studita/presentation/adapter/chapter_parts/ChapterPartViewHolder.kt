package com.example.studita.presentation.adapter.chapter_parts

import android.view.View
import com.example.studita.R
import com.example.studita.presentation.activities.ExercisesActivity
import com.example.studita.presentation.utils.getAppCompatActivity
import com.example.studita.presentation.utils.startActivity
import com.example.studita.presentation.model.ChapterPartUiModel
import kotlinx.android.synthetic.main.chapter_part_item.view.*


class ChapterPartViewHolder(view: View) : ChapterPartsViewHolder(view){
    override fun bind(model: ChapterPartUiModel) {
        with(itemView) {
            chapterPartItemText.text = model.chapterPartName
            chapterPartItemIcon.setImageResource(R.drawable.ic_lock_open_87)
            chapterPartItemButton.background =
                itemView.resources.getDrawable(R.drawable.oval_blue, context.theme)
            chapterPartItemButton.setImageResource(R.drawable.ic_play_arrow_white87)
            chapterPartItemButton.setOnClickListener {
                getAppCompatActivity()?.startActivity<ExercisesActivity>("CHAPTER_PART_NUMBER" to model.chapterPartNumber) }
        }
    }

}