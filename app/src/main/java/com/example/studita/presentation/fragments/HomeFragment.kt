package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.studita.R
import com.example.studita.domain.entity.UserDataData
import com.example.studita.presentation.activities.MainMenuActivity
import com.example.studita.presentation.adapter.levels.LevelsAdapter
import com.example.studita.presentation.draw.AvaDrawer
import com.example.studita.presentation.utils.startActivity
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.listeners.FabRecyclerImpl
import com.example.studita.presentation.listeners.FabScrollListener
import com.example.studita.presentation.model.mapper.HomeUserDataUiModelMapper
import com.example.studita.presentation.view_model.ChapterViewModel
import com.example.studita.presentation.view_model.HomeFragmentViewModel
import com.example.studita.presentation.view_model.MainFragmentViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.home_layout.*
import kotlinx.android.synthetic.main.home_layout_bar.*

class HomeFragment : BaseFragment(R.layout.home_layout), AppBarLayout.OnOffsetChangedListener, FabScrollListener{

    private var homeFragmentViewModel: HomeFragmentViewModel? = null
    private var chapterPartsViewModel: ChapterViewModel? = null
    private var mainFragmentViewModel: MainFragmentViewModel? = null
    var onThemeChangeListener: OnThemeChangeListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)
        }
        mainFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)
        }
        chapterPartsViewModel = activity?.run {
            ViewModelProviders.of(this).get(ChapterViewModel::class.java)
        }

        chapterPartsViewModel?.let {
            it.errorState.observe(viewLifecycleOwner, Observer { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            })

            it.progressState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> { done ->
                    if (done) {
                        ChapterBottomSheetFragment().show(
                            (context as AppCompatActivity).supportFragmentManager,
                            null
                        )
                    }
                })
        }

        homeFragmentViewModel?.let {
            it.progressState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> { done ->
                    if (done) {
                        mainFragmentViewModel?.showProgress(false)
                        val userDataResult = it.userDataState.value
                        homeLayoutRecyclerView.adapter =
                            LevelsAdapter(
                                it.getRecyclerItems(HomeUserDataUiModelMapper().map(userDataResult ?: UserDataData("", "",  1, 0, 0)), it.results)
                            )
                        homeLayoutRecyclerView.visibility = View.VISIBLE
                    } else {
                        homeLayoutRecyclerView.visibility = View.GONE
                    }
                })

            it.errorState.observe(viewLifecycleOwner, Observer { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            })

            it.userDataState.observe(viewLifecycleOwner, androidx.lifecycle.Observer<UserDataData>{data->
                homeLayoutBarLogInButton.visibility = View.GONE
                homeLayoutBarAccountImageView.visibility = View.VISIBLE

                if (data.avatarLink == null) {
                    AvaDrawer.drawAwa(homeLayoutBarAccountImageView, data.userName)
                } else {
                    Glide
                        .with(this@HomeFragment)
                        .load(data.avatarLink)
                        .centerCrop()
                        .apply(RequestOptions.circleCropTransform())
                        .into(homeLayoutBarAccountImageView)
                }
            })

            if(loggedIn(it.userToken, it.userId)){
                homeLayoutBarLogInButton.visibility = View.GONE
                homeLayoutBarAccountImageView.visibility = View.VISIBLE
            }
        }
        homeLayoutBarLogInButton.setOnClickListener { (activity as AppCompatActivity).startActivity<MainMenuActivity>() }
        homeLayoutBarAccountImageView.setOnClickListener { (activity as AppCompatActivity).startActivity<MainMenuActivity>() }
        homeLayoutAppBar.addOnOffsetChangedListener(this)
        homeLayoutRecyclerView.addOnScrollListener(FabRecyclerImpl(this))

        homeLayoutBarTitle.setOnClickListener { onThemeChangeListener?.onThemeChanged() }

    }

    private fun loggedIn(userToken: String?, userId: String?): Boolean{
        if((userToken != null) and (userId != null)){
            return true
        }
        return false
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
