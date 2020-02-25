package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.studita.R
import com.example.studita.domain.entity.ChapterPartData
import com.example.studita.presentation.adapter.ChapterPartsAdapter
import com.example.studita.presentation.adapter.ChapterViewHolder.Companion.returnProgressText
import com.example.studita.presentation.fragments.base.BaseBottomSheetDialogFragment
import com.example.studita.presentation.model.ChapterPartUiModel
import com.example.studita.presentation.model.LevelUiModel
import kotlinx.android.synthetic.main.chapter_parts_layout.*
import kotlinx.android.synthetic.main.chapter_parts_layout_info.*

class ChapterPartsBottomSheetFragment : BaseBottomSheetDialogFragment(R.layout.chapter_parts_layout){

    private var chapterModel: LevelUiModel.LevelChapter? = null
    private var chapterParts: List<ChapterPartUiModel>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chapterModel = arguments?.getParcelable("CHAPTER_MODEL")
        chapterParts = arguments?.getParcelableArrayList("CHAPTER_PATS")
        chapterPartsLayoutRecyclerView.adapter = chapterParts?.let { ChapterPartsAdapter(it) }

        chapterPartsLayoutTitle.text = chapterModel?.chapterTitle
        setProgressText(chapterPartsLayoutInfoTextView)

    }

    private fun setProgressText(textView: TextView){
        textView.text  = context?.let { chapterModel?.let { it1 -> returnProgressText(it1, it) } }
    }

}