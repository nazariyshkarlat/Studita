package com.example.studita.presentation.fragments.bottom_sheets

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.ChapterData
import com.example.studita.presentation.activities.ExercisesActivity
import com.example.studita.presentation.model.ChapterUiModel
import com.example.studita.presentation.model.toChapterPartUiModel
import com.example.studita.presentation.model.toChapterUiModel
import com.example.studita.presentation.view_model.ChapterViewModel
import com.example.studita.presentation.views.CustomSnackbar
import com.example.studita.presentation.views.custom_bottom_sheet.CustomBottomSheetBehavior
import com.example.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawer
import com.example.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerDialog
import com.example.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerFragment
import com.example.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.handle.BottomDrawerHandleView
import com.example.studita.utils.*
import kotlinx.android.synthetic.main.chapter_layout.*
import kotlinx.android.synthetic.main.chapter_layout_header.*
import kotlinx.android.synthetic.main.chapter_part_item.view.*
import java.io.IOException
import kotlin.math.roundToInt


class ChapterBottomSheetFragment : BottomDrawerFragment(){

    private lateinit var chapterViewModel: ChapterViewModel

    companion object {
        var needsRefresh = false
        var needsDismiss = false
        var snackbarShowReason =
            SnackbarShowReason.NONE

        enum class SnackbarShowReason {
            NONE,
            BAD_RESULT
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.chapter_layout, container, false)
    }

    override fun configureBottomDrawer(): BottomDrawerDialog {
        return BottomDrawerDialog.build(context!!) {
            isWrapContent = false
            handleView = BottomDrawerHandleView(context).apply {
                val widthHandle =
                    resources.getDimensionPixelSize(R.dimen.bottom_sheet_handle_width)
                val heightHandle =
                    resources.getDimensionPixelSize(R.dimen.bottom_sheet_handle_height)
                val params =
                    FrameLayout.LayoutParams(widthHandle, heightHandle, Gravity.CENTER_HORIZONTAL)

                params.topMargin =
                    resources.getDimensionPixelSize(R.dimen.bottom_sheet_handle_top_margin)

                layoutParams = params
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chapterViewModel = ViewModelProviders.of(this).get(ChapterViewModel::class.java)

        addBottomSheetCallback {
            onSlide { _, slideOffset ->
                val factor = with((slideOffset - 0.5F) * (1f / (1f - 0.5F))){
                    if(this < 0F)
                        0F
                    else
                        this
                }

                val titlePadding = ((if(factor > 0F) 16F.dpToPx() else 0)  + (16F.dpToPx() * factor)).roundToInt()
                chapterHeaderTitle.updatePadding(left = titlePadding,right = titlePadding)
                chapterHeaderCloseButton.alpha = factor
                chapterHeaderCloseButton.isEnabled = factor > 0
            }
        }

        chapterHeaderCloseButton.setOnClickListener {
            dismissWithBehavior()
        }

        if (savedInstanceState == null) {
            arguments?.getInt("CHAPTER_NUMBER")
                ?.let { chapterNumber -> chapterViewModel.getChapter(chapterNumber) }
        }
        chapterViewModel.progressState.observe(viewLifecycleOwner, Observer { progress ->
            if (progress) {
                val chapterUiModel = chapterViewModel.chapterData.toChapterUiModel()
                (chapterLayoutLayoutProgressBar.parent as ViewGroup).removeView(
                    chapterLayoutLayoutProgressBar
                )
                fillItems(chapterViewModel.chapterData)
                chapterLayoutNestedScrollView.visibility = View.VISIBLE
                chapterHeaderTitle.text = chapterUiModel.title
                chapterHeaderProgressText.text = getProgressText(chapterUiModel)
                chapterHeaderProgressBar.currentProgress = getProgressPercent(chapterUiModel)
            }
        })
    }

    private fun fillItems(chapterData: ChapterData){

        chapterLayoutLinearLayout.removeAllViews()

        chapterData.parts.forEach { part->
            val partUiModel = part.toChapterPartUiModel()
            val childItem = chapterLayoutLinearLayout.makeView(R.layout.chapter_part_item)
            val chapterPartInChapterNumber = chapterViewModel.chapterPartInChapterNumber(part)

            childItem.chapterPartItemText.text = partUiModel.chapterPartName

            if(chapterViewModel.chapterPartIsOpen(chapterPartInChapterNumber)){

                if(chapterViewModel.isCurrentChapterPart(chapterPartInChapterNumber, chapterData.chapterNumber))
                    setItemSelected(childItem)
                else
                    setItemEnabled(childItem)

                childItem.chapterPartItemButton.setOnClickListener {
                    (activity as AppCompatActivity).startActivity<ExercisesActivity>(
                        "CHAPTER_NUMBER" to chapterData.chapterNumber,
                        "CHAPTER_PART_NUMBER" to part.number,
                        "CHAPTER_PARTS_IN_CHAPTER_COUNT" to chapterData.parts.size,
                        "CHAPTER_NAME" to chapterData.title,
                        "EXERCISES_IN_CHAPTER_COUNT" to chapterData.exercisesCount,
                        "CHAPTER_PART_IN_CHAPTER_NUMBER" to chapterPartInChapterNumber,
                        "IS_TRAINING" to (chapterPartInChapterNumber - 1 != UserUtils.userData.completedParts[chapterData.chapterNumber - 1])
                    )
                }
            }else{
                setItemDisabled(childItem)
            }

            chapterLayoutLinearLayout.addView(childItem)
        }

    }

    override fun onStart() {
        super.onStart()
        bottomDrawerDialog?.behavior?.apply {
            topOffset =
                (resources.getDimension(R.dimen.homeLayoutToolbarHeight) + 24F.dpToPx()).toInt()
        }
    }

    override fun onResume() {
        super.onResume()

        if(needsDismiss){
            dismiss()
            needsDismiss = false
            return
        }

        if (needsRefresh) {
            updateView(chapterViewModel.chapterData)

            if (snackbarShowReason != SnackbarShowReason.NONE) {
                activity?.let {
                    val snackbar = CustomSnackbar(it)

                    val text = when (snackbarShowReason) {
                        SnackbarShowReason.BAD_RESULT -> resources.getString(R.string.exercise_snackbar_bad_result_reason)
                        else -> throw IOException("unknown show snackbar reason")
                    }

                    snackbar.show(
                        text,
                        ThemeUtils.getGreenColor(snackbar.context),
                        contentView = chapterLayoutFrameLayout.parent.parent.parent as ViewGroup,
                        duration = 5000L,
                        delay = 1000L
                    )
                    snackbarShowReason =
                        SnackbarShowReason.NONE
                }
            }
            needsRefresh = false
        }
    }

    private fun updateView(chapterData: ChapterData){
        chapterHeaderProgressText.text = getProgressText(chapterData.toChapterUiModel())
        chapterHeaderProgressBar.currentProgress = getProgressPercent(chapterData.toChapterUiModel())
        fillItems(chapterData)
    }

    private fun getProgressText(chapterUiModel: ChapterUiModel): SpannableStringBuilder {
        return LevelUtils.getProgressText(
            UserUtils.userData.completedParts[chapterUiModel.chapterNumber - 1],
            chapterUiModel.parts.size,
            context!!
        )
    }

    private fun getProgressPercent(chapterUiModel: ChapterUiModel): Float {
        return LevelUtils.getChapterProgressPercent(
            UserUtils.userData.completedParts[chapterUiModel.chapterNumber - 1],
            chapterUiModel.parts.size
        )
    }

    private fun setItemSelected(itemView: View) {
        itemView.chapterPartItemIcon.isSelected = true
        itemView.chapterPartItemButton.isSelected = true
        itemView.chapterPartItemIcon.isEnabled = true
        itemView.chapterPartItemButton.isEnabled = true
    }

    private fun setItemEnabled(itemView: View) {
        itemView.chapterPartItemIcon.isEnabled = true
        itemView.chapterPartItemButton.isEnabled = true
        itemView.chapterPartItemIcon.isSelected = false
        itemView.chapterPartItemButton.isSelected = false
    }

    private fun setItemDisabled(itemView: View) {
        itemView.chapterPartItemIcon.isEnabled = false
        itemView.chapterPartItemButton.isEnabled = false
        itemView.chapterPartItemIcon.isSelected = false
        itemView.chapterPartItemButton.isSelected = false
    }

}