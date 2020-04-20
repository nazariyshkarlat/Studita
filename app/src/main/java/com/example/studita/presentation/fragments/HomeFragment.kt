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
import com.example.studita.domain.interactor.SubscribeEmailStatus
import com.example.studita.presentation.activities.MainMenuActivity
import com.example.studita.presentation.adapter.levels.LevelsAdapter
import com.example.studita.presentation.draw.AvaDrawer
import com.example.studita.presentation.utils.startActivity
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.listeners.FabRecyclerImpl
import com.example.studita.presentation.listeners.FabScrollListener
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.utils.TextUtils
import com.example.studita.presentation.utils.TimeUtils
import com.example.studita.presentation.utils.UserUtils
import com.example.studita.presentation.view_model.ChapterViewModel
import com.example.studita.presentation.view_model.HomeFragmentViewModel
import com.example.studita.presentation.view_model.MainFragmentViewModel
import com.example.studita.presentation.views.CustomSnackbar
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.home_layout.*
import kotlinx.android.synthetic.main.home_layout_bar.*
import java.util.*

class HomeFragment : BaseFragment(R.layout.home_layout), AppBarLayout.OnOffsetChangedListener, FabScrollListener{

    companion object{
        var needsRefresh = false
    }

    private var homeFragmentViewModel: HomeFragmentViewModel? = null
    private var chapterPartsViewModel: ChapterViewModel? = null
    private var mainFragmentViewModel: MainFragmentViewModel? = null

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
        }

        homeFragmentViewModel?.let {
            it.progressState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { done ->
                    if (done) {
                        mainFragmentViewModel?.hideProgress(true)
                        homeLayoutRecyclerView.adapter =
                            LevelsAdapter(
                                it.getRecyclerItems(HomeRecyclerUiModel.HomeUserDataUiModel, it.results),
                                it)
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

                UserUtils.userData = data

                if(TimeUtils.getCalendarDayCount(Date(), data.streakDate) > 1F){
                    data.streakDays = 0
                }

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

            it.subscribeEmailState.observe(viewLifecycleOwner, androidx.lifecycle.Observer { pair ->
                val subscribe = pair.first
                val status = pair.second
                status as SubscribeEmailStatus.Success
                UserUtils.userData!!.isSubscribed = subscribe
                homeLayoutRecyclerView.adapter?.notifyItemChanged(homeLayoutRecyclerView.adapter!!.itemCount-2)
                val snackbar = CustomSnackbar(view.context)
                if(subscribe){
                    snackbar.show(resources.getString(R.string.subscribe_email, TextUtils.encryptEmail(status.result)), R.color.blue, 5000L, bottomMarginExtra = resources.getDimension(R.dimen.bottomNavigationHeight).toInt())
                }else{
                    snackbar.show(resources.getString(R.string.unsubscribe_email), R.color.blue, 3000L, bottomMarginExtra = resources.getDimension(R.dimen.bottomNavigationHeight).toInt())
                }
            })

            if(UserUtils.isLoggedIn()){
                homeLayoutBarLogInButton.visibility = View.GONE
                homeLayoutBarAccountImageView.visibility = View.VISIBLE
            }
        }
        homeLayoutBarLogInButton.setOnClickListener { (activity as AppCompatActivity).startActivity<MainMenuActivity>() }
        homeLayoutBarAccountImageView.setOnClickListener { (activity as AppCompatActivity).startActivity<MainMenuActivity>() }
        homeLayoutAppBar.addOnOffsetChangedListener(this)
        homeLayoutRecyclerView.addOnScrollListener(FabRecyclerImpl(this))
    }

    override fun onResume() {
        super.onResume()
        if(needsRefresh) {
            homeLayoutRecyclerView.adapter?.notifyDataSetChanged()
            needsRefresh = false
        }
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
}
