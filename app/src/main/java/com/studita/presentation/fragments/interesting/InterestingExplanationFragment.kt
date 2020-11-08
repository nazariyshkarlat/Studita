package com.studita.presentation.fragments.interesting

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.model.InterestingUiModelScreen
import com.studita.presentation.view_model.InterestingViewModel
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.ThemeUtils

open class InterestingExplanationFragment(viewId: Int) : NavigatableFragment(viewId),
    ViewTreeObserver.OnScrollChangedListener {

    var interestingViewModel: InterestingViewModel? = null
    var interestingExplanationModel: InterestingUiModelScreen.InterestingUiModelExplanationScreen? =
        null
    private var maxScrollY = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interestingViewModel = activity?.run {
            ViewModelProviders.of(this).get(InterestingViewModel::class.java)
        }
        interestingViewModel?.let {
            interestingExplanationModel =
                it.currentScreen as InterestingUiModelScreen.InterestingUiModelExplanationScreen
        }

        view.viewTreeObserver.addOnScrollChangedListener(this)

        if (!isHidden)
            checkScrollY()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            checkScrollY()
        } else {
            interestingViewModel?.showToolbarDivider(false)
            interestingViewModel?.showButtonDivider(false)
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
                interestingViewModel?.showButtonDivider(true)
            }
        }
    }

    private fun checkScrollY() {
        view?.let {
            it.post {
                if (it is ScrollView) {
                    val scrollY: Int = it.scrollY
                    interestingViewModel?.showToolbarDivider(scrollY != 0)
                    interestingViewModel?.showButtonDivider(scrollY != maxScrollY)
                }
            }
        }
    }

    override fun onDestroyView() {
        interestingViewModel?.showToolbarDivider(false)
        interestingViewModel?.showButtonDivider(false)
        super.onDestroyView()
    }


    private fun getMaxScrollY(scrollView: ScrollView) =
        0.coerceAtLeast(scrollView.getChildAt(0).height - (scrollView.height - scrollView.paddingBottom - scrollView.paddingTop))

}