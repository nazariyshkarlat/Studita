package com.example.studita.presentation.adapter

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.extensions.createSpannableString
import com.example.studita.presentation.extensions.getAppCompatActivity
import com.example.studita.presentation.model.LevelUiModel
import com.example.studita.presentation.fragments.ChapterPartsBottomSheetFragment
import com.example.studita.presentation.view_model.ChapterPartsViewModel
import com.example.studita.presentation.views.CustomTypefaceSpan
import kotlinx.android.synthetic.main.chapter_layout.view.*

class ChapterViewHolder(view: View) : LevelsViewHolder<LevelUiModel.LevelChapter>(view){

    companion object{
        fun returnProgressText(chapter: LevelUiModel.LevelChapter, context: Context): SpannableStringBuilder {
            val builder = SpannableStringBuilder()
            val text = context.resources.getString(
                R.string.chapter_progress,
                0,
                chapter.tasksCount
            )
            builder.append(text.substring(0, text.indexOf(" ")).createSpannableString(typeFace = ResourcesCompat.getFont(context, R.font.roboto_medium)))
            builder.append(text.substring(text.indexOf(" ")))
            return builder
        }
    }

    override fun bind(model: Any) {
        val modelData = (model as LevelUiModel.LevelChapter)
        with(itemView) {
            chapterLayoutTitle.text = modelData.chapterTitle
            chapterLayoutSubtitle.text = modelData.chapterSubtitle
            chapterLayoutProgressText.text =
                returnProgressText(modelData, context)
            setOnClickListener {
                getChapterParts(model)
            }
        }
    }

    private fun getChapterParts(chapterModel: LevelUiModel.LevelChapter){
        val viewModel = itemView.getAppCompatActivity().run{
            ViewModelProviders.of(this as AppCompatActivity).get(ChapterPartsViewModel::class.java)
        }
        viewModel.getChapterParts(chapterModel)
    }

}