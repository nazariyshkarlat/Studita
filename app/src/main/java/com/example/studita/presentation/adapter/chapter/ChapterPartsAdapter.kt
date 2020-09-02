package com.example.studita.presentation.adapter.chapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.domain.entity.ChapterData
import com.example.studita.presentation.model.ChapterPartUiModel
import com.example.studita.presentation.model.ChapterUiModel
import com.example.studita.presentation.model.toChapterPartUiModel
import com.example.studita.utils.makeView

class ChapterPartsAdapter(private val chapterData: ChapterData) :
    RecyclerView.Adapter<ChapterPartsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterPartsViewHolder =
        ChapterPartViewHolder(parent.makeView(R.layout.chapter_part_item))

    override fun onBindViewHolder(holder: ChapterPartsViewHolder, position: Int) {
        holder.bind(chapterData.parts[position].toChapterPartUiModel(), chapterData)
    }

    override fun getItemCount() = chapterData.parts.size

}


abstract class ChapterPartsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(model: ChapterPartUiModel, chapterData: ChapterData)
}