package com.example.studita.presentation.adapter.chapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.presentation.adapter.ChapterPartViewHolder
import com.example.studita.presentation.extensions.makeView
import com.example.studita.presentation.model.ChapterPartUiModel
import com.example.studita.presentation.model.ChapterUiModel

class ChapterPartsAdapter(private val chapterUiModel: ChapterUiModel) :
    RecyclerView.Adapter<ChapterPartsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterPartsViewHolder = ChapterPartViewHolder(parent.makeView(R.layout.chapter_part_item))

    override fun onBindViewHolder(holder: ChapterPartsViewHolder, position: Int) {
        holder.bind(chapterUiModel.parts[position])
    }

    override fun getItemCount() = chapterUiModel.parts.size

}


abstract class ChapterPartsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(model: ChapterPartUiModel)
}