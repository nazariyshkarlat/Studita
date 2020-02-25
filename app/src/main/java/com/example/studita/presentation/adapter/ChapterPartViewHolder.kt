package com.example.studita.presentation.adapter

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.studita.R
import com.example.studita.domain.entity.ChapterPartData
import com.example.studita.presentation.activities.ExercisesActivity
import com.example.studita.presentation.extensions.getAppCompatActivity
import com.example.studita.presentation.extensions.startActivity
import com.example.studita.presentation.fragments.ExercisesLoadFragment
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
            chapterPartItemButton.setOnClickListener { getAppCompatActivity()?.startActivity<ExercisesActivity>() }
        }
    }

}