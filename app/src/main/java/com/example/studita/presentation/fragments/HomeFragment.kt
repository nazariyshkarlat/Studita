package com.example.studita.presentation.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.activities.MainMenuActivity
import com.example.studita.presentation.adapter.LevelsAdapter
import com.example.studita.presentation.extensions.addFragment
import com.example.studita.presentation.extensions.startActivity
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.listeners.FabRecyclerImpl
import com.example.studita.presentation.listeners.FabScrollListener
import com.example.studita.presentation.view_model.ChapterPartsViewModel
import com.example.studita.presentation.view_model.HomeFragmentMainActivityFABViewModel
import com.example.studita.presentation.view_model.HomeFragmentViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.home_layout.*
import kotlinx.android.synthetic.main.home_layout_bar.*
import kotlinx.android.synthetic.main.home_layout_user_state.*
import java.util.ArrayList

class HomeFragment : BaseFragment(R.layout.home_layout), AppBarLayout.OnOffsetChangedListener, FabScrollListener{

    private var levelsViewModel: HomeFragmentViewModel? = null
    private var chapterPartsViewModel: ChapterPartsViewModel? = null
    private var fabViewModel: HomeFragmentMainActivityFABViewModel? = null
    lateinit var onThemeChangeListener: OnThemeChangeListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        levelsViewModel = activity?.run {
            ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)
        }
        fabViewModel = activity?.run {
            ViewModelProviders.of(this).get(HomeFragmentMainActivityFABViewModel::class.java)
        }
        chapterPartsViewModel = activity?.run {
            ViewModelProviders.of(this).get(ChapterPartsViewModel::class.java)
        }

        chapterPartsViewModel?.let{
            it.errorState.observe(viewLifecycleOwner, Observer{ message->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            })

            it.progressState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> { done ->
                    if(done){
                        val bundle = Bundle()
                        bundle.putParcelable("CHAPTER_MODEL", it.results.first)
                        bundle.putParcelableArrayList("CHAPTER_PATS", it.results.second as ArrayList<out Parcelable>)
                        val bottomSheet = ChapterPartsBottomSheetFragment()
                        bottomSheet.arguments = bundle
                        bottomSheet.show(
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

        homeLayoutBarTitle.setOnClickListener{onThemeChangeListener.onThemeChanged()}
    }

    override fun onScroll(
        scrollY: Int
    ) {
        if (scrollY != 0)
            homeLayoutBar.background = context?.getDrawable(R.drawable.divider_bottom_drawable)
        else
            homeLayoutBar.background = null
    }

    override fun show() {
        fabViewModel?.showFab(true)
    }

    override fun hide() {
        fabViewModel?.showFab(false)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        homeLayoutAppBar.alpha =
            (homeLayoutAppBar.height + verticalOffset * 2) / homeLayoutAppBar.height.toFloat()
    }

    enum class FABState{
        SHOW,
        HIDE,
    }

    interface OnThemeChangeListener{
        fun onThemeChanged()
    }
}
