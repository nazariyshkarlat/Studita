package com.example.studita.presentation.fragments.exercises.description

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.domain.entity.exercise.ExercisesDescriptionData
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.exercises.ExercisesScrollableFragment
import com.example.studita.presentation.view_model.ExercisesViewModel

open class ExercisesDescriptionFragment(viewId: Int) : ExercisesScrollableFragment(viewId),
    ViewTreeObserver.OnScrollChangedListener {

    var exercisesDescriptionModel: ExercisesDescriptionData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let {
            exercisesDescriptionModel = it.exercisesResponseData.exercisesDescription
        }
    }
}