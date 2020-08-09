package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.user_statistics.UserStatFragment
import com.example.studita.presentation.fragments.user_statistics.UserStatOfflineModeFragment
import com.example.studita.presentation.fragments.user_statistics.UserStatUnLoggedInFragment
import com.example.studita.presentation.view_model.FriendsFragmentViewModel
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
                    toolbarLayout.background = if (show) resources.getDrawable(
                        R.drawable.divider_bottom_drawable,
                        toolbarLayoutTitle.context.theme
                    ) else null
                })

            it.toolbarRightButtonState.observe(viewLifecycleOwner, Observer { state ->
                when (state) {
                    is ToolbarFragmentViewModel.ToolbarRightButtonState.IsEnabled -> {
                        toolbarLayoutRightButton.setImageResource(state.imageRes)
                        toolbarLayoutRightButton.isEnabled = true
                        toolbarLayoutRightButton.setOnClickListener { state.onClick.invoke(it) }
                        toolbarLayoutRightButton.visibility = View.VISIBLE
                    }
                    is ToolbarFragmentViewModel.ToolbarRightButtonState.Disabled -> {
                        toolbarLayoutRightButton.setImageResource(state.imageRes)
                        toolbarLayoutRightButton.isEnabled = false
                        toolbarLayoutRightButton.visibility = View.VISIBLE
                    }
                    is ToolbarFragmentViewModel.ToolbarRightButtonState.Invisible -> {
                        toolbarLayoutRightButton.visibility = View.GONE
                    }
                }
            })

            it.progressState.observe(viewLifecycleOwner, Observer { show ->
                if (show)
                    toolbarLayoutRightButtonProgress.visibility = View.VISIBLE
                else
                    toolbarLayoutRightButtonProgress.visibility = View.GONE
            })
        }

        (view as ViewGroup).toolbarLayoutBackButton.setOnClickListener { activity?.onBackPressed() }
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbarFragmentViewModel?.toolbarFragmentOnNavigateState?.value = null
    }

    override fun onNavigate(fragment: NavigatableFragment?) {
        toolbarFragmentViewModel?.let { viewModel ->
            viewModel.setToolbarRightButtonState(ToolbarFragmentViewModel.ToolbarRightButtonState.Invisible)
            viewModel.hideProgress()
            viewModel.hideDivider()
            viewModel.setToolbarText(
                when (fragment) {
                    is CropAvatarFragment -> resources.getString(R.string.crop_photo)
                    is AuthorizationFragment -> resources.getString(R.string.authorization)
                    is ProfileMenuFragment -> resources.getString(R.string.account)
                    is PrivacySettingsFragment, is PrivacySettingsOfflineModeFragment -> resources.getString(
                        R.string.privacy
                    )
                    is EditProfileFragment, is EditProfileOfflineModeFragment -> resources.getString(
                        R.string.edit_profile
                    )
                    is UserStatFragment, is UserStatUnLoggedInFragment, is UserStatOfflineModeFragment -> resources.getString(
                        R.string.stat
                    )
                    is MyProfileFragment, is MyProfileOfflineModeFragment -> resources.getString(R.string.my_profile)
                    is ProfileFragment -> resources.getString(R.string.profile)
                    is MyFriendsFragment, is MyFriendsOfflineModeFragment -> {

                        if (fragment is MyFriendsFragment) {
                            if (fragment.friendsFragmentViewModel.searchState != FriendsFragmentViewModel.SearchState.NoSearch)
                                resources.getString(R.string.search)
                            else
                                resources.getString(R.string.my_friends)
                        } else
                            resources.getString(R.string.my_friends)

                    }
                    is FriendsFragment -> {
                        if (fragment.friendsFragmentViewModel.searchState != FriendsFragmentViewModel.SearchState.NoSearch)
                            resources.getString(R.string.search)
                        else
                            resources.getString(R.string.friends)
                    }
                    is NotificationsFragment, is NotificationsOfflineModeFragment -> resources.getString(
                        R.string.notifications
                    )
                    else -> null
                }
            )
        }
    }

}