package com.example.studita.presentation.fragments.base

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.studita.presentation.extensions.navigateBack
import com.example.studita.presentation.fragments.currentFragment

open class NavigatableFragment(viewId: Int) : BaseFragment(viewId){
    lateinit var listener: Animator.AnimatorListener

    companion object {
        var onNavigateFragment: OnNavigateFragment? = null
    }

    fun onBackClick() {
        (activity as AppCompatActivity).navigateBack(this)
        (view?.parent as ViewGroup).alpha = 0F
        (view?.parent as ViewGroup).animate().alpha(1F).setListener(null).setDuration(200).start()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        currentFragment = this
        onNavigateFragment?.onNavigate(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            currentFragment = this
            onNavigateFragment?.onNavigate(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            listener = object : Animator.AnimatorListener{
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                }

            }
            view.alpha = 0F
            view.animate().alpha(1F).setDuration(200).setListener(listener).start()
    }

    interface OnNavigateFragment {
        fun onNavigate(fragment: NavigatableFragment?)
    }

    override fun onDestroy() {
        super.onDestroy()
        onNavigateFragment?.onNavigate(null)
    }
}