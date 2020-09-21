package com.example.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.di.data.exercise.ExerciseResultModule
import com.example.studita.domain.entity.exercise.ExerciseReportData
import com.example.studita.domain.entity.exercise.ExerciseReportRequestData
import com.example.studita.domain.entity.exercise.ExerciseReportType
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import kotlinx.coroutines.launch

class ExerciseReportBugBottomSheetFragmentViewModel(private val exerciseNumber: Int) : ViewModel(){

    private val exerciseResultInteractor = ExerciseResultModule.getExerciseResultInteractorImpl()

    var isThxLayout = false

    val selectedItems = ArrayList<ExerciseReportType>()

    fun sendReport(){
        isThxLayout = true
        viewModelScope.launch {
            exerciseResultInteractor.sendExerciseReport(ExerciseReportRequestData(UserUtils.getUserIDTokenData(), ExerciseReportData(PrefsUtils.getUserId(), exerciseNumber, selectedItems)))
        }
    }


}