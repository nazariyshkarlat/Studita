package com.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.di.data.exercise.ExerciseResultModule
import com.studita.domain.entity.exercise.ExerciseReportData
import com.studita.domain.entity.exercise.ExerciseReportRequestData
import com.studita.domain.entity.exercise.ExerciseReportType
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ExerciseReportBugBottomSheetFragmentViewModel(private val exerciseNumber: Int) : ViewModel(){

    private val exerciseResultInteractor = ExerciseResultModule.getExerciseResultInteractorImpl()

    var isThxLayout = false

    val selectedItems = ArrayList<ExerciseReportType>()

    fun sendReport(){
        isThxLayout = true
        GlobalScope.launch(Dispatchers.Main) {
            exerciseResultInteractor.sendExerciseReport(ExerciseReportRequestData(UserUtils.getUserIDTokenData(), ExerciseReportData(exerciseNumber, selectedItems)))
        }
    }


}