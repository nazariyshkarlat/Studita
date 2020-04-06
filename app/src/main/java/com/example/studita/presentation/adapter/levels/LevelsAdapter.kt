package com.example.studita.presentation.adapter.levels

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.utils.makeView
import java.lang.UnsupportedOperationException

class LevelsAdapter(private val items: List<HomeRecyclerUiModel>) :
    RecyclerView.Adapter<LevelsViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelsViewHolder<out HomeRecyclerUiModel> = when (viewType) {
        LevelsViewType.USER_STATE.ordinal -> HomeUserDataViewHolder(parent.makeView(R.layout.home_layout_user_data))
        LevelsViewType.LEVEL.ordinal -> LevelViewHolder(parent.makeView(R.layout.level_item))
        LevelsViewType.CHAPTER.ordinal -> ChapterViewHolder(parent.makeView(R.layout.chapter_item))
        LevelsViewType.INTERESTING.ordinal -> InterestingViewHolder(parent.makeView(R.layout.interesting_item))
        else -> throw UnsupportedOperationException("unknown type of item")
    }

    override fun onBindViewHolder(holder: LevelsViewHolder<out HomeRecyclerUiModel>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is HomeRecyclerUiModel.HomeUserDataUiModel -> LevelsViewType.USER_STATE.ordinal
            is HomeRecyclerUiModel.LevelNumber -> LevelsViewType.LEVEL.ordinal
            is HomeRecyclerUiModel.LevelChapterUiModel -> LevelsViewType.CHAPTER.ordinal
            is HomeRecyclerUiModel.LevelInterestingUiModel -> LevelsViewType.INTERESTING.ordinal
        }

    override fun getItemCount() = items.size+1

}


abstract class LevelsViewHolder<T : HomeRecyclerUiModel>(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(model: HomeRecyclerUiModel)
}

private enum class LevelsViewType {
    USER_STATE,
    LEVEL,
    CHAPTER,
    INTERESTING
}