package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.adapter.chapter_parts.ChapterPartsAdapter
import com.example.studita.presentation.adapter.levels.ChapterViewHolder.Companion.returnProgressText
import com.example.studita.presentation.fragments.base.BaseBottomSheetDialogFragment
import com.example.studita.presentation.model.ChapterUiModel
import com.example.studita.presentation.view_model.ChapterViewModel
import kotlinx.android.synthetic.main.chapter_layout.*
import kotlinx.android.synthetic.main.chapter_layout_info.*


class ChapterBottomSheetFragment : BaseBottomSheetDialogFragment(R.layout.chapter_layout){

    private var chapterModel: ChapterUiModel? = null
    private var chapterViewModel: ChapterViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chapterViewModel = activity?.run {
            ViewModelProviders.of(this).get(ChapterViewModel::class.java)
        }

        chapterViewModel?.let{
            chapterModel =  it.results
            chapterLayoutRecyclerView.adapter = chapterModel?.let {model -> ChapterPartsAdapter(model) }

            chapterLayoutTitle.text = chapterModel?.title
            setProgressText(chapterLayoutInfoTextView)
        }

    }

    private fun setProgressText(textView: TextView){
        textView.text  = context?.let { chapterModel?.let { it1 -> returnProgressText(it1.parts.size, it) } }
    }

}