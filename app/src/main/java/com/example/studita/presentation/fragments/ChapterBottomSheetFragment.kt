package com.example.studita.presentation.fragments

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.adapter.chapter.ChapterPartsAdapter
import com.example.studita.presentation.fragments.base.BaseBottomSheetDialogFragment
import com.example.studita.presentation.model.ChapterUiModel
import com.example.studita.utils.LevelUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.getAppCompatActivity
import com.example.studita.presentation.view_model.ChapterViewModel
import com.example.studita.presentation.views.CustomSnackbar
import kotlinx.android.synthetic.main.chapter_layout.*
import kotlinx.android.synthetic.main.chapter_layout_header.*
import java.io.IOException


class ChapterBottomSheetFragment : BaseBottomSheetDialogFragment(R.layout.chapter_layout){

    private var chapterViewModel: ChapterViewModel? = null
    private var chapterUiModel: ChapterUiModel? = null

    companion object{
        var needsRefresh = false
        var snackbarShowReason = SnackbarShowReason.NONE

        enum class SnackbarShowReason{
            NONE,
            CHAPTER_COMPLETED,
            BAD_RESULT,
            CHAPTER_COMPLETED_AND_BAD_RESULT
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chapterViewModel = ViewModelProviders.of(this).get(ChapterViewModel::class.java)

        chapterViewModel?.let {
            if(savedInstanceState == null) {
                arguments?.getInt("CHAPTER_NUMBER")
                    ?.let { chapterNumber -> it.getChapter(chapterNumber) }
            }
            it.progressState.observe(viewLifecycleOwner, Observer { progress ->
                if (progress) {
                    chapterUiModel =  it.results
                    chapterLayoutRecyclerView.adapter = chapterUiModel?.let {model -> ChapterPartsAdapter(model) }
                    (chapterLayoutLayoutProgressBar.parent as ViewGroup).removeView(
                        chapterLayoutLayoutProgressBar
                    )
                    chapterLayoutNestedScrollView.visibility = View.VISIBLE
                    chapterHeaderTitle.text = chapterUiModel?.title
                    chapterHeaderProgressText.text = getProgressText()
                    chapterHeaderProgressBar.percentProgress = getProgressPercent() ?: 0F
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        if(needsRefresh) {
            updateView()

            if(snackbarShowReason != SnackbarShowReason.NONE){
                activity?.let {
                    val snackbar = CustomSnackbar(it)

                    val text = when(snackbarShowReason){
                        SnackbarShowReason.BAD_RESULT -> resources.getString(R.string.exercise_snackbar_bad_result_reason)
                        SnackbarShowReason.CHAPTER_COMPLETED -> resources.getString(R.string.exercise_snackbar_chapter_completed_reason)
                        SnackbarShowReason.CHAPTER_COMPLETED_AND_BAD_RESULT -> "${resources.getString(R.string.exercise_snackbar_chapter_completed_reason)} ${resources.getString(R.string.exercise_snackbar_bad_result_reason)}"
                        else -> throw IOException("unknown show snackbar reason")
                    }

                    snackbar.show(text, ContextCompat.getColor(snackbar.context, R.color.green), contentView = chapterLayoutFrameLayout.parent.parent.parent as ViewGroup, duration = 5000L, delay = 1000L)
                    snackbarShowReason = SnackbarShowReason.NONE
                }
            }
        }
    }

    private fun updateView(){
        chapterHeaderProgressText.text = getProgressText()
        chapterHeaderProgressBar.percentProgress = getProgressPercent() ?: 0F
        chapterLayoutRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun getProgressText(): SpannableStringBuilder?{
        return context?.let {context->
            chapterUiModel?.let { chapterUiModel ->
                LevelUtils.getProgressText(
                    UserUtils.userData.completedParts[chapterUiModel.chapterNumber-1],
                    chapterUiModel.parts.size,
                    context
                )
            }
        }
    }

    private fun getProgressPercent(): Float?{
        return chapterUiModel?.let { chapterUiModel ->
                LevelUtils.getChapterProgressPercent(
                    UserUtils.userData.completedParts[chapterUiModel.chapterNumber-1],
                    chapterUiModel.parts.size
                )
            }
    }

}