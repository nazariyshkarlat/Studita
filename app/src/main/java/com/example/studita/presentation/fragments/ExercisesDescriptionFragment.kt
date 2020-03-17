package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.data.entity.exercise.ExercisesDescription
import com.example.studita.domain.entity.exercise.ExercisesDescriptionData
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercises_description_1_layout.*

open class ExercisesDescriptionFragment(viewId: Int) : NavigatableFragment(viewId), ViewTreeObserver.OnScrollChangedListener{

    var exercisesViewModel: ExercisesViewModel? = null
    var exercisesDescriptionModel: ExercisesDescriptionData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }
        exercisesViewModel?.let {
            exercisesDescriptionModel = it.exercisesResponseData.exercisesDescription
            if(!this.isHidden){
                view.viewTreeObserver.addOnScrollChangedListener(this)
                OneShotPreDrawListener.add(view) {
                    if (view.height < (view as ViewGroup).getChildAt(
                            0
                        ).height
                        + view.paddingTop + view.paddingBottom
                    ) {
                        exercisesViewModel?.showButtonDivider(true)
                    }
                }
            }
        }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            checkScrollY()
        }else {
            exercisesViewModel?.showToolbarDivider(false)
            exercisesViewModel?.showButtonDivider(false)
            view?.viewTreeObserver?.removeOnScrollChangedListener(this)
        }
    }

    override fun onScrollChanged() {
        checkScrollY()
    }

    private fun checkScrollY(){
        view?.let {
            if(it is ScrollView) {
                val scrollY: Int = it.scrollY
                exercisesViewModel?.showToolbarDivider(scrollY != 0)
            }
        }
    }

}