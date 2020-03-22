package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.adapter.chapters.ChapterPartsAdapter
import com.example.studita.presentation.adapter.levels.ChapterViewHolder.Companion.returnProgressText
import com.example.studita.presentation.fragments.base.BaseBottomSheetDialogFragment
import com.example.studita.presentation.model.ChapterUiModel
import com.example.studita.presentation.view_model.ChapterPartsViewModel
import kotlinx.android.synthetic.main.chapter_parts_layout.*
import kotlinx.android.synthetic.main.chapter_parts_layout_info.*


class ChapterPartsBottomSheetFragment : BaseBottomSheetDialogFragment(R.layout.chapter_parts_layout){

    private var chapterModel: ChapterUiModel? = null
    private var chapterPartsViewModel: ChapterPartsViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chapterPartsViewModel = activity?.run {
            ViewModelProviders.of(this).get(ChapterPartsViewModel::class.java)
        }

        chapterPartsViewModel?.let{
            chapterModel =  it.results
            chapterPartsLayoutRecyclerView.adapter = chapterModel?.let {model -> ChapterPartsAdapter(model) }

            chapterPartsLayoutTitle.text = chapterModel?.title
            setProgressText(chapterPartsLayoutInfoTextView)
        }

    }

    private fun setProgressText(textView: TextView){
        textView.text  = context?.let { chapterModel?.let { it1 -> returnProgressText(it1.parts.size, it) } }
    }

}