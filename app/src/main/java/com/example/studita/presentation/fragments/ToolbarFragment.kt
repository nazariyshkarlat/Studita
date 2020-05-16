package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ToolbarFragmentViewModel
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*

class ToolbarFragment : BaseFragment(R.layout.toolbar_layout),
    NavigatableFragment.OnNavigateFragment {
    private var toolbarFragmentViewModel: ToolbarFragmentViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(ToolbarFragmentViewModel::class.java)
        }

        toolbarFragmentViewModel?.let {

            it.toolbarFragmentOnNavigateState.value = this

            it.toolbarTextState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { text ->
                    toolbarLayoutTitle.text = text
                })

            it.toolbarDividerState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { show ->
                    toolbarLayout.background =  if(show) resources.getDrawable(R.drawable.divider_bottom_drawable, toolbarLayoutTitle.context.theme) else null
                })

            it.toolbarRightButtonState.observe(viewLifecycleOwner, Observer {onClick->
                if(onClick != null){
                    toolbarLayoutRightButton.setOnClickListener { onClick.invoke() }
                    toolbarLayoutRightButton.visibility = View.VISIBLE
                }else
                    toolbarLayoutRightButton.visibility = View.GONE
            })
        }

        (view as ViewGroup).toolbarLayoutBackButton.setOnClickListener { activity?.onBackPressed() }
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbarFragmentViewModel?.toolbarFragmentOnNavigateState?.value = null
    }

    override fun onNavigate(fragment: NavigatableFragment?) {
        toolbarFragmentViewModel?.toolbarDividerState?.value = false
        toolbarFragmentViewModel?.hideRightButton()
        toolbarFragmentViewModel?.let {
            when (fragment) {
                is AuthorizationFragment -> it.setToolbarText(resources.getString(R.string.authorization))
                is ProfileMenuFragment -> it.setToolbarText(resources.getString(R.string.account))
                is EditProfileFragment -> it.setToolbarText(resources.getString(R.string.edit_profile))
                else -> it.setToolbarText(null)
            }
        }
    }

}