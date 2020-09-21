package com.example.studita.presentation.fragments.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseReportType
import com.example.studita.presentation.view_model.ExerciseReportBugBottomSheetFragmentViewModel
import com.example.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerDialog
import com.example.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerFragment
import kotlinx.android.synthetic.main.exercise_report_bug_layout.*
import java.lang.UnsupportedOperationException

class ExerciseReportBugBottomSheetFragment : BottomDrawerFragment(){

    private lateinit var viewModel: ExerciseReportBugBottomSheetFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.exercise_report_bug_layout, container, false)
    }

    override fun configureBottomDrawer(): BottomDrawerDialog {
        return BottomDrawerDialog.build(context!!) {
            isWrapContent = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
                return ExerciseReportBugBottomSheetFragmentViewModel(arguments!!.getInt("EXERCISE_NUMBER")) as T
            }
        })[ExerciseReportBugBottomSheetFragmentViewModel::class.java]

        restoreSelectedState()
        setItemsOnClick()

        if(viewModel.isThxLayout){
            formThxLayout()
        }

        exerciseReportBugLayoutButton.setOnClickListener {

            if(!viewModel.isThxLayout) {
                viewModel.sendReport()
                formThxLayout()
                animateThxLayoutAlpha()
            }else
                dismissWithBehavior()
        }
    }

    private fun setItemsOnClick(){
        exerciseReportBugLayoutContentLayout.children.forEach {child->
            if(child is ViewGroup && child.children.any { it is AppCompatCheckBox}){
                val reportType = when(child.id){
                    R.id.exerciseReportBugLayoutCantUnderstandExercise -> ExerciseReportType.CANT_UNDERSTAND
                    R.id.exerciseReportBugLayoutExerciseHasMistake -> ExerciseReportType.EXERCISE_MISTAKE
                    R.id.exerciseReportBugLayoutMyAnswerIsCorrect -> ExerciseReportType.ANSWER_IS_CORRECT
                    R.id.exerciseReportBugLayoutMyAnswerIsIncorrect -> ExerciseReportType.ANSWER_IS_INCORRECT
                    else -> throw UnsupportedOperationException("unknown exercise report type")
                }

                child.setOnClickListener {
                    val checkBox = (child.children.first { it is AppCompatCheckBox } as AppCompatCheckBox)
                    checkBox.isChecked = !checkBox.isChecked

                    if(checkBox.isChecked)
                        viewModel.selectedItems.add(reportType)
                    else
                        viewModel.selectedItems.remove(reportType)
                }
            }
        }
    }

    private fun restoreSelectedState(){
        viewModel.selectedItems.forEach {
            val itemView = when(it){
                ExerciseReportType.CANT_UNDERSTAND -> exerciseReportBugLayoutCantUnderstandExercise
                ExerciseReportType.EXERCISE_MISTAKE -> exerciseReportBugLayoutExerciseHasMistake
                ExerciseReportType.ANSWER_IS_CORRECT -> exerciseReportBugLayoutMyAnswerIsCorrect
                ExerciseReportType.ANSWER_IS_INCORRECT -> exerciseReportBugLayoutMyAnswerIsIncorrect
            }

            (itemView.children.first { it is AppCompatCheckBox } as AppCompatCheckBox).isChecked = true
        }
    }

    private fun animateThxLayoutAlpha(){
        exerciseReportBugLayoutThanksLayout.alpha = 0F
        exerciseReportBugLayoutThanksLayout.animate().alpha(1F).start()
    }

    private fun formThxLayout(){
        exerciseReportBugLayoutContentLayout.visibility = View.INVISIBLE
        exerciseReportBugLayoutThanksLayout.visibility = View.VISIBLE
        exerciseReportBugLayoutButton.text = resources.getString(R.string.exercise_report_bug_thx_button_text)
    }
}