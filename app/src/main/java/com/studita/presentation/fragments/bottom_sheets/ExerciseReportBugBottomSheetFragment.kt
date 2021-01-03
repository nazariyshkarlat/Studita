package com.studita.presentation.fragments.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseReportType
import com.studita.presentation.view_model.ExerciseReportBugBottomSheetFragmentViewModel
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerDialog
import com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerFragment
import com.studita.utils.makeView
import kotlinx.android.synthetic.main.exercise_report_bug_layout.*
import kotlinx.android.synthetic.main.exercise_report_bug_thx_layout.*
import java.lang.UnsupportedOperationException

class ExerciseReportBugBottomSheetFragment : BottomDrawerFragment(){

    private val viewModel: ExerciseReportBugBottomSheetFragmentViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
                return ExerciseReportBugBottomSheetFragmentViewModel(arguments!!.getInt("EXERCISE_NUMBER")) as T
            }
        })[ExerciseReportBugBottomSheetFragmentViewModel::class.java]
    }

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

        val exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let{
            viewModel.isThxLayout = exercisesViewModel.feedbackWasSent
        }

        if(exercisesViewModel?.getSnackbarState()?.second?.exerciseResult == true){
            exerciseReportBugLayoutMyAnswerIsCorrect.visibility = View.GONE
        }else
            exerciseReportBugLayoutMyAnswerIsIncorrect.visibility = View.GONE

        if(viewModel.isThxLayout){
                formThxLayout()
            }else{
                restoreSelectedState()
                setItemsOnClick()
            }

        exerciseReportBugLayoutButton.setOnClickListener {

            if(!viewModel.isThxLayout) {
                exercisesViewModel?.feedbackWasSent = true
                viewModel.sendReport()
                formThxLayout()
                animateThxLayoutAlpha()
            }else
                dismissWithBehavior()
        }

        exerciseReportBugLayoutButton.isEnabled = viewModel.selectedItems.isNotEmpty() || viewModel.isThxLayout
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

                    exerciseReportBugLayoutButton.isEnabled = viewModel.selectedItems.isNotEmpty()
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
        val thxLayout = (view as ViewGroup).makeView(R.layout.exercise_report_bug_thx_layout)

        exerciseReportBugLayoutContentLayout.visibility = View.INVISIBLE
        exerciseReportBugLayoutButton.text =
            resources.getString(R.string.exercise_report_bug_thx_button_text)

        (view as ViewGroup).addView(thxLayout)

    }
}