package com.example.studita.presentation.fragments.base

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.studita.R
import com.example.studita.presentation.utils.navigateBack

open class NavigatableFragment(viewId: Int) : BaseFragment(viewId){
    lateinit var listener: Animator.AnimatorListener

    companion object {
        var onNavigateFragment: OnNavigateFragment? = null
    }

    open fun onBackClick() {
        (activity as AppCompatActivity).navigateBack(this)
        (view?.parent as ViewGroup).alpha = 0F
        (view?.parent as ViewGroup).animate().alpha(1F).setDuration(resources.getInteger(
            R.integer.navigatable_fragment_anim_duration).toLong()).start()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onNavigateFragment?.onNavigate(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            onNavigateFragment?.onNavigate(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.alpha = 0F
        view.animate().alpha(1F).setDuration(resources.getInteger(
            R.integer.navigatable_fragment_anim_duration).toLong()).start()
    }

    interface OnNavigateFragment {
        fun onNavigate(fragment: NavigatableFragment?)
    }

    override fun onDestroy() {
        super.onDestroy()
        onNavigateFragment?.onNavigate(null)
    }
}