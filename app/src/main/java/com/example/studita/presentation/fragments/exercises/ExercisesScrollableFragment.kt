package com.example.studita.presentation.fragments.exercises

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.domain.entity.exercise.ExercisesDescriptionData
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ExercisesViewModel

open class ExercisesScrollableFragment(viewId: Int) : NavigatableFragment(viewId),
    ViewTreeObserver.OnScrollChangedListener {

    var exercisesViewModel: ExercisesViewModel? = null
    private var maxScrollY = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        view.viewTreeObserver.addOnScrollChangedListener(this)
        if (!isHidden) {
            view.post {
                checkScrollY()
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            checkScrollY()
        } else {
            exercisesViewModel?.showToolbarDivider(false)
            exercisesViewModel?.showButtonDivider(false)
            view?.viewTreeObserver?.removeOnScrollChangedListener(this)
        }
    }

    override fun onScrollChanged() {
        checkScrollY()
    }

    protected fun checkButtonDivider(view: View) {
        OneShotPreDrawListener.add(view) {
            maxScrollY = getMaxScrollY(view as ScrollView)
            if (view.height < (view as ViewGroup).getChildAt(
                    0
                ).height
                + view.paddingTop + view.paddingBottom
            ) {
                exercisesViewModel?.showButtonDivider(true)
            }
        }
    }

    private fun checkScrollY() {
        view?.let {
            if (it is ScrollView) {
                val scrollY: Int = it.scrollY
                exercisesViewModel?.showToolbarDivider(scrollY != 0)
                exercisesViewModel?.showButtonDivider(scrollY != maxScrollY)
            }
        }
    }

    private fun getMaxScrollY(scrollView: ScrollView) =
        0.coerceAtLeast(scrollView.getChildAt(0).height - (scrollView.height - scrollView.paddingBottom - scrollView.paddingTop))

    override fun onDestroyView() {
        view?.viewTreeObserver?.removeOnScrollChangedListener(this)
        exercisesViewModel?.showToolbarDivider(false)
        exercisesViewModel?.showButtonDivider(false)
        super.onDestroyView()
    }
}