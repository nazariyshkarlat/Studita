package com.example.studita.presentation.fragments.interesting

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import com.example.studita.R
import com.example.studita.presentation.view_model.InterestingViewModel
import com.example.studita.utils.ThemeUtils
import com.example.studita.utils.dpToPx
import kotlinx.android.synthetic.main.interesting_explanation_layout.*

class InterestingExplanation1Fragment :
    InterestingExplanationFragment(R.layout.interesting_explanation_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interestingExplanationModel?.let {
            formView(it.textParts)
            checkButtonDivider(view)
        }

        interestingExplanationLayoutThumbUp.setOnClickListener {
            if(!it.isSelected) {
                interestingViewModel?.setFeedback(InterestingViewModel.FeedbackState.LIKE)
            }
        }
        interestingExplanationLayoutThumbDown.setOnClickListener {
            if(!it.isSelected) {
                interestingViewModel?.setFeedback(InterestingViewModel.FeedbackState.DISLIKE)
            }
        }

        interestingViewModel?.feedbackState?.observe(viewLifecycleOwner, Observer {
            if(it == InterestingViewModel.FeedbackState.LIKE){
                interestingExplanationLayoutThumbUp.isSelected = true
                interestingExplanationLayoutThumbDown.isSelected = false
            }else if(it == InterestingViewModel.FeedbackState.DISLIKE){
                interestingExplanationLayoutThumbUp.isSelected = false
                interestingExplanationLayoutThumbDown.isSelected = true
            }
        })
    }

    private fun formView(textParts: List<String>) {
        val textColor = context?.let { ThemeUtils.getPrimaryColor(it) } ?: 0
        val margin = 16.dpToPx()
        textParts.forEachIndexed { index, s ->
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val textView = TextView(context)
            textView.text = s
            textView.textSize = 18F
            textView.setTextColor(textColor)
            params.topMargin = margin
            textView.layoutParams = params
            interestingExplanationLayoutParentLinearLayout.addView(textView, index + 1)
        }
    }
}