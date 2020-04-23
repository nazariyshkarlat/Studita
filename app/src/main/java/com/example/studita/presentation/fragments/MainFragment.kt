package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.studita.R
import com.example.studita.presentation.activities.DefaultActivity
import com.example.studita.presentation.activities.MainActivity
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.utils.*
import com.example.studita.presentation.view_model.MainActivityNavigationViewModel
import com.example.studita.presentation.view_model.MainFragmentViewModel
import kotlinx.android.synthetic.main.bottom_navigation.*
import kotlinx.android.synthetic.main.frame_layout.view.*
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment : BaseFragment(R.layout.main_layout){

    private var navigationViewModel : MainActivityNavigationViewModel? = null
    private var mainFragmentViewModel: MainFragmentViewModel? = null
    var transValue = 0F

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainActivityNavigationViewModel::class.java)
        }
        mainFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)
        }

        navigationViewModel?.let {viewModel ->
            viewModel.navigationState.observe(
                this,
                Observer {
                    it?.let { pair ->

                        if (pair.first == MainActivityNavigationViewModel.BottomNavigationEnum.CLOSE_APP)
                            (activity as AppCompatActivity).finish()

                        val fragment = (activity as AppCompatActivity).supportFragmentManager.findFragmentByTag(it.second)

                        if (fragment != null) {

                            showHideFabOnNavigation(fragment)

                            when (pair.first) {
                                MainActivityNavigationViewModel.BottomNavigationEnum.REMOVE -> (activity as AppCompatActivity).removeFragment(
                                    fragment
                                )
                                MainActivityNavigationViewModel.BottomNavigationEnum.SHOW -> (activity as AppCompatActivity).showFragment(
                                    fragment
                                )
                                MainActivityNavigationViewModel.BottomNavigationEnum.HIDE -> (activity as AppCompatActivity).hideFragment(
                                    fragment
                                )
                                else -> throw UnsupportedOperationException("unknown navigation enum")
                            }
                        } else {
                            if (pair.first == MainActivityNavigationViewModel.BottomNavigationEnum.ADD) {
                                val addFragment =
                                    Class.forName(pair.second).newInstance() as Fragment
                                (activity as AppCompatActivity).addFragment(
                                    addFragment,
                                    R.id.mainLayoutFrameLayout
                                )
                                showHideFabOnNavigation(addFragment)
                            }
                        }
                    }
                })

            viewModel.navigationSelectedIdState.observe(this, Observer<Int> { id ->
                bottomNavigationView.selectedItemId = id
            })
        }
        mainFragmentViewModel?.let { viewModel ->
                viewModel.fabState.observe(this, Observer<Boolean> { show ->
                if (show) {
                    mainLayoutFAB.animate().translationY(0F).alpha(1F)
                        .setInterpolator(DecelerateInterpolator(2F)).start()
                } else {
                    mainLayoutFAB.animate().translationY(transValue).alpha(
                        0F
                    ).setInterpolator(AccelerateInterpolator(2F)).start()
                }
            })

            viewModel.progressState.observe(this, Observer{ hide ->
                if(hide)
                    (view as ViewGroup).removeView(mainLayoutProgressBar)
            })
        }

        mainLayoutFAB.onViewCreated {
            transValue =
                mainLayoutFAB.height.toFloat() + mainLayoutFAB.marginBottom + bottomNavigationView.height
        }

        if(savedInstanceState != null){
            showHideFabOnNavigation((activity as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.mainLayoutFrameLayout))
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationViewModel)

        handleNetworkChanges(view)
    }

    private fun showHideFabOnNavigation(fragment: Fragment?){
        if (fragment is HomeFragment) {
            mainLayoutFAB.show()
        } else {
            mainLayoutFAB.hide()
        }
    }


    private fun handleNetworkChanges(view: View) {
        NetworkUtils.getNetworkLiveData(view.context).observe(this, Observer { isConnected ->
                val snackbar = connectionSnackbarLayout as TextView
                if (!isConnected) {
                    snackbar.text = resources.getString(R.string.network_absent)
                    snackbar.setBackgroundColor(ContextCompat.getColor(view.context, R.color.gray88))
                    mainLayoutBottomSection.animate().translationY(0F)
                        .setDuration(resources.getInteger(R.integer.snackbar_anim_duration).toLong())
                        .start()
                } else {
                    snackbar.text = resources.getString(R.string.back_online)
                    snackbar.setBackgroundColor(
                        ContextCompat.getColor(
                            view.context,
                            R.color.green
                        )
                    )
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(resources.getInteger(R.integer.back_online_snackbar_duration).toLong())
                        mainLayoutBottomSection.animate()
                            .translationY(resources.getDimension(R.dimen.connectionSnackbarHeight))
                            .setDuration(
                                resources.getInteger(R.integer.snackbar_anim_duration).toLong()
                            ).start()
                    }
                }
            })
    }

}