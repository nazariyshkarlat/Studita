package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.NavigatableFragment.Companion.onNavigateFragment
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.MainMenuActivityViewModel
import kotlinx.android.synthetic.main.exercise_toolbar.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*

var currentFragment: NavigatableFragment? = null

class ToolbarFragment : BaseFragment(R.layout.toolbar_layout),
    NavigatableFragment.OnNavigateFragment {
    private var viewModel: MainMenuActivityViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onNavigateFragment = this
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainMenuActivityViewModel::class.java)
        }

        viewModel?.let {
            it.toolbarTextState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { text ->
                    toolbarLayoutTitle.text = text
                })
        }

        (view as ViewGroup).toolbarLayoutBackButton.setOnClickListener { activity?.onBackPressed() }
        currentFragment?.let { onNavigate(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        onNavigateFragment = null
    }

    override fun onNavigate(fragment: NavigatableFragment) {
        if(fragment is AuthorizationFragment)
            toolbarLayoutTitle.text = resources.getString(R.string.authorization)
        else
            toolbarLayoutTitle.text = null
        viewModel?.let {
            if(view?.height != 0)
                it.toolbarHeight = view?.height ?: 0
        }
    }

}