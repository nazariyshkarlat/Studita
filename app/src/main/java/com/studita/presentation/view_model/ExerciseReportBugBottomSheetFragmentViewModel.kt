package com.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import com.studita.domain.entity.exercise.ExerciseReportData
import com.studita.domain.entity.exercise.ExerciseReportRequestData
import com.studita.domain.entity.exercise.ExerciseReportType
import com.studita.domain.interactor.exercises.ExerciseResultInteractor
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.utils.UserUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class ExerciseReportBugBottomSheetFragmentViewModel(private val exerciseNumber: Int) : ViewModel(){

    private val exerciseResultInteractor = GlobalContext.get().get<ExerciseResultInteractor>()

    var isThxLayout = false

    val selectedItems = ArrayList<ExerciseReportType>()

    fun sendReport(){
        isThxLayout = true
        GlobalScope.launch(Dispatchers.Main) {
            exerciseResultInteractor.sendExerciseReport(ExerciseReportRequestData(UserUtils.getUserIDTokenData(), ExerciseReportData(exerciseNumber, selectedItems)))
        }
    }


}