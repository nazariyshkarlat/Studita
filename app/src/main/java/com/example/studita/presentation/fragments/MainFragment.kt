package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.activities.DefaultActivity
import com.example.studita.presentation.extensions.*
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.view_model.HomeFragmentMainActivityFABViewModel
import com.example.studita.presentation.view_model.MainActivityNavigationViewModel
import kotlinx.android.synthetic.main.bottom_navigation.*
import kotlinx.android.synthetic.main.main_layout.*
import java.lang.NullPointerException
import java.lang.UnsupportedOperationException

class MainFragment : BaseFragment(R.layout.main_layout){

    private var navigationViewModel : MainActivityNavigationViewModel? = null
    private var FABViewModel: HomeFragmentMainActivityFABViewModel? = null
    var transValue = 0F

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainActivityNavigationViewModel::class.java)
        }
        FABViewModel = activity?.run {
            ViewModelProviders.of(this).get(HomeFragmentMainActivityFABViewModel::class.java)
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

        FABViewModel?.fabState?.observe(this, Observer<HomeFragment.FABState> { state ->
            println(state)
            when (state) {
                HomeFragment.FABState.HIDE -> mainLayoutFAB.animate().translationY(transValue).alpha(
                    0F
                ).setInterpolator(AccelerateInterpolator(2F)).start()
                HomeFragment.FABState.SHOW -> mainLayoutFAB.animate().translationY(0F).alpha(1F)
                    .setInterpolator(DecelerateInterpolator(2F)).start()
                else -> throw NullPointerException("FABState is null")
            }
        })

        mainLayoutFAB.onViewCreated {
            transValue =
                mainLayoutFAB.height.toFloat() + mainLayoutFAB.marginBottom + bottomNavigationView.height
        }

        if(savedInstanceState != null){
            val homeFragment: HomeFragment? = (activity as AppCompatActivity).supportFragmentManager.findFragmentByTag(HomeFragment::class.java.name) as HomeFragment?
            homeFragment?.onThemeChangeListener = activity as DefaultActivity
            showHideFabOnNavigation((activity as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.mainLayoutFrameLayout))
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationViewModel)
        mainLayoutFrameLayout.setOnClickListener{ println((activity as AppCompatActivity).supportFragmentManager.fragments) }
    }

    private fun showHideFabOnNavigation(fragment: Fragment?){
        if (fragment is HomeFragment) {
            mainLayoutFAB.show()
        } else {
            mainLayoutFAB.hide()
        }
    }

}