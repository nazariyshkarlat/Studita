package com.studita.presentation.adapter.levels

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.studita.R
import com.studita.presentation.model.HomeRecyclerUiModel
import com.studita.presentation.model.toHomeRecyclerItems
import com.studita.presentation.view_model.HomeFragmentViewModel
import com.studita.utils.PrefsUtils
import com.studita.utils.makeView

class LevelsAdapter(
    private val items: ArrayList<HomeRecyclerUiModel>,
    private val vm: HomeFragmentViewModel,
    private val lifecycleOwner: LifecycleOwner
) :  RecyclerView.Adapter<LevelsViewHolder<*>>(), LevelViewHolder.ChangeLevelItemsVisibilityCallback {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LevelsViewHolder<out HomeRecyclerUiModel> = when (viewType) {
        LevelsViewType.USER_STATE.ordinal -> {HomeUserDataViewHolder(parent.makeView(R.layout.home_layout_user_data), lifecycleOwner)}
        LevelsViewType.LEVEL.ordinal -> LevelViewHolder(parent.makeView(R.layout.level_item), this)
        LevelsViewType.CHAPTER.ordinal -> ChapterViewHolder(
            parent.makeView(R.layout.chapter_item),
            items.count { it is HomeRecyclerUiModel.LevelChapterUiModel }, lifecycleOwner)
        else -> throw UnsupportedOperationException("unknown type of item")
    }

    override fun onBindViewHolder(
        holder: LevelsViewHolder<out HomeRecyclerUiModel>,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is HomeRecyclerUiModel.HomeUserDataUiModel -> LevelsViewType.USER_STATE.ordinal
            is HomeRecyclerUiModel.HomeRecyclerLevelViewModel -> LevelsViewType.LEVEL.ordinal
            is HomeRecyclerUiModel.LevelChapterUiModel -> LevelsViewType.CHAPTER.ordinal
        }

    override fun getItemCount() = items.size

    override fun onLevelItemsVisibilityChanged(levelNumber: Int, isExpanded: Boolean) {
        items.first { ((it is HomeRecyclerUiModel.HomeRecyclerLevelViewModel && it.levelNumber == levelNumber))}.let{
            (it as HomeRecyclerUiModel.HomeRecyclerLevelViewModel).isExpanded = isExpanded
            notifyItemChanged(items.indexOf(it))
        }
        if(!isExpanded){
            PrefsUtils.saveHomeLayoutCollapsedLevel(levelNumber)

            items.filter { (it is HomeRecyclerUiModel.LevelChapterUiModel && it.levelNumber == levelNumber) }.map {
                items.indexOf(it) to it
            }.let {
                it.forEach { pair->
                    items.remove(pair.second)
                }
                notifyItemRangeRemoved(it.map { it.first }.minOrNull()!! ,it.size)
            }
        }else{
            PrefsUtils.removeHomeLayoutCollapsedLevel(levelNumber)

            vm.getRecyclerItems(
                HomeRecyclerUiModel.HomeUserDataUiModel,
                vm.results!!.map { it.toHomeRecyclerItems() }.flatten(),
            ).let {allItems->
                allItems.filter { (it is HomeRecyclerUiModel.LevelChapterUiModel && it.levelNumber == levelNumber) }
                    .mapIndexed { idx, item ->
                        allItems.indexOf(item) to item
                    }.let {
                        it.forEach { pair ->
                            items.add(pair.first, pair.second)
                        }
                        notifyItemRangeInserted(it.map { it.first }.minOrNull()!! + 1, it.size)
                    }
            }
        }
    }

}


abstract class LevelsViewHolder<T : HomeRecyclerUiModel>(view: View) :
    RecyclerView.ViewHolder(view) {

    abstract fun bind(model: HomeRecyclerUiModel)
}

private enum class LevelsViewType {
    USER_STATE,
    LEVEL,
    CHAPTER
}