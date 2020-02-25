package com.example.studita.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.presentation.extensions.makeView
import com.example.studita.presentation.model.LevelUiModel
import java.lang.UnsupportedOperationException

class LevelsAdapter(private val items: List<LevelUiModel>) :
    RecyclerView.Adapter<LevelsViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelsViewHolder<*>  = when (viewType) {
        LevelsViewType.USER_STATE.ordinal -> UserStateViewHolder(parent.makeView(R.layout.home_layout_user_state))
        LevelsViewType.LEVEL.ordinal -> LevelViewHolder(parent.makeView(R.layout.level_layout))
        LevelsViewType.CHAPTER.ordinal -> ChapterViewHolder(parent.makeView(R.layout.chapter_layout))
        else -> throw UnsupportedOperationException("unknown type of item")
    }

    override fun onBindViewHolder(holder: LevelsViewHolder<*>, position: Int) {
        if(position != 0)
            holder.bind(items[position-1])
        else
            holder.bind(Any())
    }

    override fun getItemViewType(position: Int): Int =
        if(position == 0){
                LevelsViewType.USER_STATE.ordinal
            }else{
                when (items[position-1]) {
                    is LevelUiModel.LevelNumber -> LevelsViewType.LEVEL.ordinal
                    is LevelUiModel.LevelChapter -> LevelsViewType.CHAPTER.ordinal
                }
        }

    override fun getItemCount() = items.size+1

}


abstract class LevelsViewHolder<T : LevelUiModel?>(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(model: Any)
}

private enum class LevelsViewType {
    USER_STATE,
    LEVEL,
    CHAPTER
}