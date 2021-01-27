package com.studita.presentation.adapter.levels

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.studita.presentation.fragments.bottom_sheets.ChapterBottomSheetFragment
import com.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.studita.presentation.model.HomeRecyclerUiModel
import com.studita.presentation.views.AlphaGradientView
import com.studita.utils.LevelUtils
import com.studita.utils.UserUtils
import com.studita.utils.UserUtils.observeNoNull
import com.studita.utils.dp
import kotlinx.android.synthetic.main.chapter_item.view.*

class ChapterViewHolder(view: View, val count: Int, private val lifecycleOwner: LifecycleOwner) :
    LevelsViewHolder<HomeRecyclerUiModel.LevelChapterUiModel>(view) {

    override fun bind(model: HomeRecyclerUiModel) {
        model as HomeRecyclerUiModel.LevelChapterUiModel
        with(itemView) {
            chapterItemTitle.text = model.chapterTitle
            chapterItemSubtitle.text = model.chapterSubtitle
            UserUtils.userDataLiveData.observeNoNull(lifecycleOwner, Observer {
                chapterItemProgressText.text = LevelUtils.getProgressText(
                    it.completedParts[model.chapterNumber - 1],
                    model.chapterPartsCount,
                    context
                )
                chapterItemProgressBar.currentProgress  = LevelUtils.getChapterProgressPercent(
                    it.completedParts[model.chapterNumber - 1],
                    model.chapterPartsCount
                )
            })
            setOnSingleClickListener {
                initBottomSheetFragment(model.chapterNumber, context)
            }
        }
    }

    private fun initBottomSheetFragment(chapterNumber: Int, context: Context) {
        val bottomSheetFragment =
            ChapterBottomSheetFragment()
        val bundle = Bundle()
        bundle.putInt("CHAPTER_NUMBER", chapterNumber)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(
            (context as AppCompatActivity).supportFragmentManager,
            null
        )
    }

}