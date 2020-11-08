package com.studita.presentation.fragments.bottom_sheets

import android.content.DialogInterface
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.studita.R
import com.studita.domain.entity.ChapterData
import com.studita.domain.entity.UserDataData
import com.studita.presentation.activities.ExercisesActivity
import com.studita.presentation.fragments.error_fragments.ChapterBottomSheetInternetIsDisabledFragment
import com.studita.presentation.fragments.error_fragments.ChapterBottomSheetServerProblemsFragment
import com.studita.presentation.fragments.error_fragments.InternetIsDisabledFragment
import com.studita.presentation.fragments.error_fragments.ServerProblemsFragment
import com.studita.presentation.listeners.ReloadPageCallback
import com.studita.presentation.model.ChapterUiModel
import com.studita.presentation.model.toChapterPartUiModel
import com.studita.presentation.model.toChapterUiModel
import com.studita.presentation.view_model.ChapterViewModel
import com.studita.presentation.view_model.ExerciseReportBugBottomSheetFragmentViewModel
import com.studita.presentation.views.CustomSnackbar
import com.studita.presentation.views.CustomSnackbar.Companion.hide
import com.studita.presentation.views.custom_bottom_sheet.CustomBottomSheetBehavior
import com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawer
import com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerDialog
import com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerFragment
import com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.handle.BottomDrawerHandleView
import com.studita.utils.*
import com.studita.utils.UserUtils.observeNoNull
import kotlinx.android.synthetic.main.chapter_layout.*
import kotlinx.android.synthetic.main.chapter_layout_header.*
import kotlinx.android.synthetic.main.chapter_part_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToInt


class ChapterBottomSheetFragment : BottomDrawerFragment(), ReloadPageCallback{

    private val chapterViewModel: ChapterViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
                return ChapterViewModel(arguments!!.getInt("CHAPTER_NUMBER")) as T
            }
        })[ChapterViewModel::class.java]
    }

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


        addBottomSheetCallback {
            onSlide { _, slideOffset ->

                if (this@ChapterBottomSheetFragment.view != null) {
                    if (slideOffset < -0.5F) {
                        CustomSnackbar.hide(chapterLayoutFrameLayout.parent.parent.parent.parent as ViewGroup)
                    }

                    val factor = with((slideOffset - 0.5F) * (1f / (1f - 0.5F))) {
                        if (this < 0F)
                            0F
                        else
                            this
                    }

                    val titlePadding =
                        ((if (factor > 0F) 16F.dpToPx() else 0) + (12F.dpToPx() * factor)).roundToInt()
                    chapterHeaderTitle.updatePadding(left = titlePadding, right = titlePadding)
                    chapterHeaderCloseButton.alpha = factor
                    chapterHeaderCloseButton.isEnabled = factor > 0
                }
            }
        }

        addChildViews()

        chapterHeaderCloseButton.setOnClickListener {
            dismissWithBehavior()
        }


        chapterViewModel.progressState.observe(viewLifecycleOwner, Observer { progress ->
            if (progress) {
                    UserUtils.userDataLiveData.observeNoNull(viewLifecycleOwner, Observer {
                        chapterLayoutLayoutProgressBar.visibility = View.GONE
                        chapterLayoutNestedScrollView.visibility = View.VISIBLE
                        val chapterUiModel = chapterViewModel.chapterData.toChapterUiModel()
                        chapterHeaderTitle.text = chapterUiModel.title
                        fillView(chapterViewModel.chapterData, it)
                        checkShowSnackbar()
                    })
            }else{
                chapterLayoutLayoutProgressBar.visibility = View.VISIBLE
                chapterLayoutNestedScrollView.visibility = View.GONE
            }
        })

        chapterViewModel.errorEvent.observe(viewLifecycleOwner, Observer {
            val isNetworkError = it
            if (isNetworkError) {
                addFragment(ChapterBottomSheetInternetIsDisabledFragment(), R.id.chapterLayoutFrameLayout, false)
            }else{
                addFragment(ChapterBottomSheetServerProblemsFragment(), R.id.chapterLayoutFrameLayout, false)
            }
        })

    }

    private fun checkShowSnackbar(){
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
                    contentView = chapterLayoutFrameLayout.parent.parent.parent.parent as ViewGroup,
                    duration = 5000L,
                    delay = 1000L
                )
                snackbarShowReason =
                    SnackbarShowReason.NONE
            }
        }
    }

    private fun addChildViews(){
        chapterLayoutLinearLayout.removeAllViews()

        (0..8).forEach { _ ->
            val childItem = chapterLayoutLinearLayout.makeView(R.layout.chapter_part_item)
            childItem.visibility = View.GONE
            chapterLayoutLinearLayout.addView(childItem)
        }
    }

    private fun fillItems(chapterData: ChapterData){

        chapterData.parts.forEachIndexed { idx, part->
            val partUiModel = part.toChapterPartUiModel()
            val childItem = chapterLayoutLinearLayout.getChildAt(idx)
            childItem.visibility = View.VISIBLE
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
    }

    private fun fillView(chapterData: ChapterData, userDataData: UserDataData){
        view?.post {
            if(this.view != null) {
                chapterHeaderProgressText.text =
                    getProgressText(chapterData.toChapterUiModel(), userDataData)
                chapterHeaderProgressBar.currentProgress =
                    getProgressPercent(chapterData.toChapterUiModel(), userDataData)
                fillItems(chapterData)
            }
        }
    }

    private fun getProgressText(chapterUiModel: ChapterUiModel, userDataData: UserDataData): SpannableStringBuilder {
        return LevelUtils.getProgressText(
            userDataData.completedParts[chapterUiModel.chapterNumber - 1],
            chapterUiModel.parts.size,
            context!!
        )
    }

    private fun getProgressPercent(chapterUiModel: ChapterUiModel, userDataData: UserDataData): Float {
        return LevelUtils.getChapterProgressPercent(
            userDataData.completedParts[chapterUiModel.chapterNumber - 1],
            chapterUiModel.parts.size
        )
    }

    private fun setItemSelected(itemView: View) {
        itemView.chapterPartItemIcon.isSelected = true
        itemView.chapterPartItemButton.isSelected = true
        itemView.chapterPartItemText.isEnabled = true
        itemView.chapterPartItemIcon.isEnabled = true
        itemView.chapterPartItemButton.isEnabled = true
    }

    private fun setItemEnabled(itemView: View) {
        itemView.chapterPartItemIcon.isEnabled = true
        itemView.chapterPartItemButton.isEnabled = true
        itemView.chapterPartItemText.isEnabled = true
        itemView.chapterPartItemIcon.isSelected = false
        itemView.chapterPartItemButton.isSelected = false
    }

    private fun setItemDisabled(itemView: View) {
        itemView.chapterPartItemIcon.isEnabled = false
        itemView.chapterPartItemButton.isEnabled = false
        itemView.chapterPartItemText.isEnabled = false
        itemView.chapterPartItemIcon.isSelected = false
        itemView.chapterPartItemButton.isSelected = false
    }

    override fun onPageReload() {
        chapterViewModel.getChapter()
    }

}