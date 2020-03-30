package com.example.studita.presentation.adapter.levels

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.utils.createSpannableString
import com.example.studita.presentation.utils.getAppCompatActivity
import com.example.studita.presentation.model.LevelUiModel
import com.example.studita.presentation.view_model.ChapterViewModel
import kotlinx.android.synthetic.main.chapter_item.view.*

class ChapterViewHolder(view: View) : LevelsViewHolder<LevelUiModel.LevelChapterUiModel>(view){

    companion object{
        fun returnProgressText(chapterPartsCount: Int, context: Context): SpannableStringBuilder {
            val completedParts = 0
            val builder = SpannableStringBuilder()
            if(completedParts != chapterPartsCount) {
                val text = context.resources.getString(
                    R.string.chapter_progress,
                    0,
                    chapterPartsCount
                )
                builder.append(text)
            }else {
                val text = context.resources.getString(
                    R.string.chapter_full_progress
                )
                builder.append(text.substring(0, text.indexOf(" ")))
                builder.append(
                    text.substring(
                        text.indexOf(" ")
                    ).createSpannableString(
                        color = ContextCompat.getColor(context, R.color.green)
                    )
                )
            }
            return builder
        }
    }

    override fun bind(model: LevelUiModel?) {
        val modelData = (model as LevelUiModel.LevelChapterUiModel)
        with(itemView) {
            chapterItemTitle.text = modelData.chapterTitle
            chapterItemSubtitle.text = modelData.chapterSubtitle
            chapterItemProgressText.text =
                returnProgressText(modelData.chapterPartsCount, context)
            setOnClickListener {
                getChapterParts(model.chapterNumber)
            }
        }
    }

    private fun getChapterParts(chapterNumber: Int){
        val viewModel = itemView.getAppCompatActivity().run{
            ViewModelProviders.of(this as AppCompatActivity).get(ChapterViewModel::class.java)
        }
        viewModel.getChapter(chapterNumber)
    }

}