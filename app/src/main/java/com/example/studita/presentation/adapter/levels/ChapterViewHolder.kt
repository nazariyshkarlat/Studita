package com.example.studita.presentation.adapter.levels

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.studita.presentation.fragments.bottom_sheets.ChapterBottomSheetFragment
import com.example.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.views.AlphaGradientView
import com.example.studita.utils.LevelUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.dpToPx
import kotlinx.android.synthetic.main.chapter_item.view.*

class ChapterViewHolder(view: View, val count: Int, private val lifecycleOwner: LifecycleOwner) :
    LevelsViewHolder<HomeRecyclerUiModel.LevelChapterUiModel>(view) {

    override fun bind(model: HomeRecyclerUiModel) {
        model as HomeRecyclerUiModel.LevelChapterUiModel
        with(itemView.chapterItemParentView) {
            chapterItemTitle.text = model.chapterTitle
            chapterItemSubtitle.text = model.chapterSubtitle
            if (model.chapterNumber == count) {
                formClosedChapter()
            } else {
                formOpenChapter()

                UserUtils.userDataLiveData.observe(lifecycleOwner, Observer {
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

    private fun formClosedChapter() {
        itemView as AlphaGradientView
        if (!itemView.fadeTop) {
            with(itemView) {
                chapterItemParentView.chapterItemProgressText.visibility = View.GONE
                chapterItemParentView.chapterItemProgressBar.visibility = View.GONE
                chapterItemParentView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = 0
                }
                chapterItemParentView.isClickable = false
                fadeTop = true
                setGradientSizeTop(0)
            }
        }
    }

    private fun formOpenChapter() {
        itemView as AlphaGradientView
        if (itemView.fadeTop) {
            with(itemView){
                chapterItemParentView.chapterItemProgressText.visibility = View.VISIBLE
                chapterItemParentView.chapterItemProgressBar.visibility = View.VISIBLE
                chapterItemParentView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = 8F.dpToPx()
                }
                chapterItemParentView.isClickable = true
                fadeTop = false
            }
        }
    }

}