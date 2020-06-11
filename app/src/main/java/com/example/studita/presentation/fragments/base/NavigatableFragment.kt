package com.example.studita.presentation.fragments.base

import android.animation.Animator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.presentation.view_model.ToolbarFragmentViewModel
import com.example.studita.utils.hideKeyboard
import com.example.studita.utils.navigateBack
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class NavigatableFragment(viewId: Int) : BaseFragment(viewId),  ViewTreeObserver.OnScrollChangedListener{
    lateinit var listener: Animator.AnimatorListener

    var toolbarFragmentViewModel: ToolbarFragmentViewModel? = null

    var onNavigateFragment: OnNavigateFragment? = null

    var scrollingView: View? = null
        set(value) {
            field = value
            if (scrollingView is RecyclerView)
                setRecyclerScrollListener()
            else
                scrollingView?.viewTreeObserver?.addOnScrollChangedListener(this)
            if(!isHidden) {
                scrollingView?.let {
                    it.post {
                        checkScroll()
                    }
                }
            }
        }

    open fun onBackClick() {
        animateAlpha(view?.parent as ViewGroup)
        (activity as AppCompatActivity).hideKeyboard()
        val f = (activity as AppCompatActivity).navigateBack(this)
        if(f is NavigatableFragment)
            onNavigateFragment?.onNavigate(f)
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
        if((view.parent as ViewGroup).childCount > 1){
            view.alpha = 0F
            view.animate().alpha(1F).setDuration(
                    resources.getInteger(
                            R.integer.navigatable_fragment_anim_duration
                    ).toLong()
            ).start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scrollingView?.viewTreeObserver?.removeOnScrollChangedListener(this)
    }

    override fun onScrollChanged() {
        if(!isHidden) {
            checkScroll()
        }
    }

    private fun setRecyclerScrollListener(){
        (scrollingView as RecyclerView).addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var scrollY = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrollY += dy
                if (!isHidden) {
                    checkScroll()
                }
            }
        })
    }

    open fun checkScroll(){
        val scrollY = if(scrollingView is RecyclerView) (scrollingView as RecyclerView).computeVerticalScrollOffset() else scrollingView?.scrollY ?: 0
        toolbarFragmentViewModel?.toolbarDividerState?.value = scrollY != 0
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            checkScroll()
        }
    }
}