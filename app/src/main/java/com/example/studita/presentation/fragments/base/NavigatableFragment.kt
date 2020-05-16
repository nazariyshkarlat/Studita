package com.example.studita.presentation.fragments.base

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.utils.navigateBack
import com.example.studita.presentation.view_model.ToolbarFragmentViewModel

open class NavigatableFragment(viewId: Int) : BaseFragment(viewId){
    lateinit var listener: Animator.AnimatorListener

    var toolbarFragmentViewModel: ToolbarFragmentViewModel? = null

    var onNavigateFragment: OnNavigateFragment? = null

    open fun onBackClick() {
        (activity as AppCompatActivity).navigateBack(this)
        animateAlpha(view?.parent as ViewGroup)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            onNavigateFragment?.onNavigate(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(ToolbarFragmentViewModel::class.java)
        }
        toolbarFragmentViewModel?.let{
            it.toolbarFragmentOnNavigateState.observe(viewLifecycleOwner, Observer {onNavigateFragment->
                this.onNavigateFragment = onNavigateFragment
                onNavigateFragment?.onNavigate(this)
            })
        }
        animateAlpha(view)
    }

    interface OnNavigateFragment {
        fun onNavigate(fragment: NavigatableFragment?)
    }


    private fun animateAlpha(view: View){
       if((view.parent as ViewGroup).childCount > 0){
            view.alpha = 0F
            view.animate().alpha(1F).setDuration(
                resources.getInteger(
                    R.integer.navigatable_fragment_anim_duration
                ).toLong()
            ).start()
        }
    }
}