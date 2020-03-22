package com.example.studita.presentation.adapter.levels

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.extensions.createSpannableString
import com.example.studita.presentation.extensions.getAppCompatActivity
import com.example.studita.presentation.model.LevelUiModel
import com.example.studita.presentation.view_model.ChapterPartsViewModel
import kotlinx.android.synthetic.main.chapter_item.view.*

class ChapterViewHolder(view: View) : LevelsViewHolder<LevelUiModel.LevelChapterUiModel>(view){

    companion object{
        fun returnProgressText(chapterPartsCount: Int, context: Context): SpannableStringBuilder {
            val builder = SpannableStringBuilder()
            val text = context.resources.getString(
                R.string.chapter_progress,
                0,
                chapterPartsCount
            )
            builder.append(text.substring(0, text.indexOf(" ")).createSpannableString(typeFace = ResourcesCompat.getFont(context, R.font.roboto_medium)))
            builder.append(text.substring(text.indexOf(" ")))
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
            ViewModelProviders.of(this as AppCompatActivity).get(ChapterPartsViewModel::class.java)
        }
        viewModel.getChapterParts(chapterNumber)
    }

}