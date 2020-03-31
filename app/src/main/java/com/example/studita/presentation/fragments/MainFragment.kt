package com.example.studita.presentation.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.studita.R
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.di.NetworkModule
import com.example.studita.presentation.activities.DefaultActivity
import com.example.studita.presentation.utils.*
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.view_model.MainActivityNavigationViewModel
import com.example.studita.presentation.view_model.MainFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.bottom_navigation.*
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.UnsupportedOperationException

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
                                if (addFragment is HomeFragment) {
                                    addFragment.onThemeChangeListener = activity as DefaultActivity
                                }
                                showHideFabOnNavigation(addFragment)
                            }
                        }
                    }
                })
        }

        navigationViewModel?.navigationSelectedIdState?.observe(this, Observer<Int> { id ->
            bottomNavigationView.selectedItemId = id
        })

        mainFragmentViewModel?.let {
            it.fabState.observe(this, Observer<Boolean> { show ->
                if (!show) {
                    mainLayoutFAB.animate().translationY(transValue).alpha(
                        0F
                    ).setInterpolator(AccelerateInterpolator(2F)).start()
                } else {
                    mainLayoutFAB.animate().translationY(0F).alpha(1F)
                        .setInterpolator(DecelerateInterpolator(2F)).start()
                }
            })
            it.progressState.observe(this, Observer<Boolean> { show ->
                if(!show)
                    (view as ViewGroup).removeView(mainLayoutProgressBar)
            })
        }

        mainLayoutFAB.onViewCreated {
            val params = mainLayoutFAB.layoutParams as LinearLayout.LayoutParams
            transValue =
                mainLayoutFAB.height.toFloat() + params.bottomMargin + bottomNavigationView.height
        }

        if(savedInstanceState != null){
            val homeFragment: HomeFragment? = (activity as AppCompatActivity).supportFragmentManager.findFragmentByTag(HomeFragment::class.java.name) as HomeFragment?
            homeFragment?.onThemeChangeListener = activity as DefaultActivity
        }
        showHideFabOnNavigation((activity as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.mainLayoutFrameLayout))

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationViewModel)

        handleNetworkChanges(view)
    }


    private fun handleNetworkChanges(view: View) {
        NetworkUtils.getNetworkLiveData(view.context)
            .observe(this, Observer { isConnected ->
                val snackbar = connectionSnackbarLayout as TextView
                snackbar.textSize = resources.getDimension(R.dimen.connectionSnackbarTextSize).toInt().pxToSp().toFloat()
                    if (!isConnected) {
                    snackbar.text = resources.getString(R.string.network_absent)
                    snackbar.setBackgroundColor(ContextCompat.getColor(view.context, R.color.gray88))
                    mainLayoutBottomSection.animate().translationY(0F).start()
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
                        mainLayoutBottomSection.animate().translationY(resources.getDimension(R.dimen.connectionSnackbarHeight)).start()
                    }
                }
            })
    }

    private fun showHideFabOnNavigation(fragment: Fragment?){
        if (fragment is HomeFragment) {
            mainLayoutFAB.show()
        } else {
            mainLayoutFAB.hide()
        }
    }

}