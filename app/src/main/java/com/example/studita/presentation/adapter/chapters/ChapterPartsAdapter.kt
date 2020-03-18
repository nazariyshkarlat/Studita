package com.example.studita.presentation.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.domain.entity.ChapterPartData
import com.example.studita.presentation.extensions.makeView
import com.example.studita.presentation.model.ChapterPartUiModel

class ChapterPartsAdapter(private val items: List<ChapterPartUiModel>) :
    RecyclerView.Adapter<ChapterPartsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterPartsViewHolder = ChapterPartViewHolder(parent.makeView(R.layout.chapter_part_item))

    override fun onBindViewHolder(holder: ChapterPartsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

}


abstract class ChapterPartsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(model: ChapterPartUiModel)
}