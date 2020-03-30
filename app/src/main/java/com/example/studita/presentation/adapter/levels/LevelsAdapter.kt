package com.example.studita.presentation.adapter.levels

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.presentation.adapter.InterestingViewHolder
import com.example.studita.presentation.adapter.LevelViewHolder
import com.example.studita.presentation.utils.makeView
import com.example.studita.presentation.model.LevelUiModel
import java.lang.UnsupportedOperationException

class LevelsAdapter(private val items: List<LevelUiModel>) :
    RecyclerView.Adapter<LevelsViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelsViewHolder<*> = when (viewType) {
        LevelsViewType.USER_STATE.ordinal -> UserStateViewHolder(parent.makeView(R.layout.home_layout_user_state))
        LevelsViewType.LEVEL.ordinal -> LevelViewHolder(parent.makeView(R.layout.level_item))
        LevelsViewType.CHAPTER.ordinal -> ChapterViewHolder(parent.makeView(R.layout.chapter_item))
        LevelsViewType.INTERESTING.ordinal -> InterestingViewHolder(parent.makeView(R.layout.interesting_item))
        else -> throw UnsupportedOperationException("unknown type of item")
    }

    override fun onBindViewHolder(holder: LevelsViewHolder<*>, position: Int) {
        if(position != 0)
            holder.bind(items[position-1])
        else
            holder.bind(null)
    }

    override fun getItemViewType(position: Int): Int =
        if(position == 0){
                LevelsViewType.USER_STATE.ordinal
            }else{
                when (items[position-1]) {
                    is LevelUiModel.LevelNumber -> LevelsViewType.LEVEL.ordinal
                    is LevelUiModel.LevelChapterUiModel -> LevelsViewType.CHAPTER.ordinal
                    is LevelUiModel.LevelInterestingUiModel -> LevelsViewType.INTERESTING.ordinal
                }
        }

    override fun getItemCount() = items.size+1

}


abstract class LevelsViewHolder<T : LevelUiModel?>(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(model: LevelUiModel?)
}

private enum class LevelsViewType {
    USER_STATE,
    LEVEL,
    CHAPTER,
    INTERESTING
}