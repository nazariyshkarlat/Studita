package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.activities.MainMenuActivity
import com.example.studita.presentation.adapter.levels.LevelsAdapter
import com.example.studita.presentation.draw.AwaDrawer
import com.example.studita.presentation.utils.startActivity
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.listeners.FabRecyclerImpl
import com.example.studita.presentation.listeners.FabScrollListener
import com.example.studita.presentation.view_model.ChapterViewModel
import com.example.studita.presentation.view_model.HomeFragmentViewModel
import com.example.studita.presentation.view_model.MainFragmentViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.home_layout.*
import kotlinx.android.synthetic.main.home_layout_bar.*

class HomeFragment : BaseFragment(R.layout.home_layout), AppBarLayout.OnOffsetChangedListener, FabScrollListener{

    private var levelsViewModel: HomeFragmentViewModel? = null
    private var chapterPartsViewModel: ChapterViewModel? = null
    private var mainFragmentViewModel: MainFragmentViewModel? = null
    var onThemeChangeListener: OnThemeChangeListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        levelsViewModel = activity?.run {
            ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)
        }
        mainFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)
        }
        chapterPartsViewModel = activity?.run {
            ViewModelProviders.of(this).get(ChapterViewModel::class.java)
        }

        chapterPartsViewModel?.let{
            it.errorState.observe(viewLifecycleOwner, Observer{ message->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            })

            it.progressState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> { done ->
                    if(done){
                        ChapterBottomSheetFragment().show(
                            (context as AppCompatActivity).supportFragmentManager,
                            null
                        )
                    }
                })
        }

        levelsViewModel?.let {
            it.progressState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> { done ->
                    if (done) {
                        mainFragmentViewModel?.showProgress(false)
                        homeLayoutRecyclerView.adapter =
                            LevelsAdapter(it.results)
                        homeLayoutRecyclerView.visibility = View.VISIBLE
                    } else {
                        homeLayoutRecyclerView.visibility = View.GONE
                    }
                })

            it.errorState.observe(viewLifecycleOwner, Observer{ message->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            })
        }
        homeLayoutBarLogInButton.setOnClickListener { (activity as AppCompatActivity).startActivity<MainMenuActivity>() }
        homeLayoutAppBar.addOnOffsetChangedListener(this)
        homeLayoutRecyclerView.addOnScrollListener(FabRecyclerImpl(this))

        homeLayoutBarTitle.setOnClickListener{onThemeChangeListener?.onThemeChanged()}
    }

    override fun onScroll(
        scrollY: Int
    ) {
        homeLayoutBar.background = if (scrollY != 0) context?.getDrawable(R.drawable.divider_bottom_drawable) else null
    }

    override fun show() {
        mainFragmentViewModel?.showFab(true)
    }

    override fun hide() {
        mainFragmentViewModel?.showFab(false)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        homeLayoutAppBar.alpha =
            (homeLayoutAppBar.height + verticalOffset * 2) / homeLayoutAppBar.height.toFloat()
    }

    interface OnThemeChangeListener{
        fun onThemeChanged()
    }
}
