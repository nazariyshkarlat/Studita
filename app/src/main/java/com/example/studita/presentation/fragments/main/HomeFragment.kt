package com.example.studita.presentation.fragments.main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import com.example.studita.App
import com.example.studita.R
import com.example.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.example.studita.domain.interactor.SubscribeEmailResultStatus
import com.example.studita.presentation.activities.MainMenuActivity
import com.example.studita.presentation.adapter.levels.LevelsAdapter
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.fragments.dialog_alerts.ChapterCompletedDialogAlertFragment
import com.example.studita.presentation.fragments.error_fragments.InternetIsDisabledMainFragment
import com.example.studita.presentation.fragments.error_fragments.ServerProblemsMainFragment
import com.example.studita.presentation.listeners.FabRecyclerImpl
import com.example.studita.presentation.listeners.FabScrollListener
import com.example.studita.presentation.listeners.ReloadPageCallback
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.view_model.HomeFragmentViewModel
import com.example.studita.presentation.view_model.MainFragmentViewModel
import com.example.studita.presentation.views.CustomSnackbar
import com.example.studita.utils.*
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.home_layout.*
import kotlinx.android.synthetic.main.home_layout_bar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment(R.layout.home_layout), AppBarLayout.OnOffsetChangedListener,
    FabScrollListener, ReloadPageCallback {

    val homeFragmentViewModel: HomeFragmentViewModel? by lazy {
        activity?.run {
            ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)
        }
    }
    private var mainFragmentViewModel: MainFragmentViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)
        }

        homeFragmentViewModel?.let {vm->


            App.authenticationState.observe(viewLifecycleOwner, Observer {pair->
                val checkTokenIsCorrect = pair.first
                val isOfflineModeChanged = pair.second

                if (isOfflineModeChanged) {

                    when (checkTokenIsCorrect) {
                        is CheckTokenIsCorrectStatus.Waiting -> {
                            if(!(PrefsUtils.isOfflineModeEnabled() && vm.results?.isNotEmpty() == true)) {
                                getLevels(vm)
                            }
                        }
                        is CheckTokenIsCorrectStatus.Correct -> {
                            if(PrefsUtils.isOfflineModeEnabled()){
                                getLevels(vm)
                            }else{
                                if(vm.levelsJob?.isActive == false && vm.results.isNullOrEmpty()){
                                    getLevels(vm)
                                }
                            }
                        }

                    }

                    App.authenticationState.value = checkTokenIsCorrect to false
                }
            })

            vm.localUserDataState.observe(viewLifecycleOwner, Observer {
                homeLayoutBarAccountImageView.fillAvatar(it.avatarLink, it.userName!!, it.userId!!)
            })

            vm.progressState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { done ->
                    if (done) {
                        homeLayoutBarLogInButton.isEnabled = true
                        homeLayoutProgressBar.visibility = View.GONE

                        homeLayoutRecyclerView.adapter =
                            LevelsAdapter(
                                vm.getRecyclerItems(
                                    HomeRecyclerUiModel.HomeUserDataUiModel,
                                    vm.results!!,
                                ),
                                vm,
                                viewLifecycleOwner
                            )
                        homeLayoutRecyclerView.visibility = View.VISIBLE

                        if(UserUtils.isLoggedIn())
                            vm.initSubscribeEmailState()

                        if(arguments?.containsKey("IS_AFTER_SIGN_UP") == true){
                            vm.showLogInSnackbar(arguments!!.getBoolean("IS_AFTER_SIGN_UP"))
                            arguments?.clear()
                        }
                    } else {

                        if(!PrefsUtils.offlineDataIsCached())
                            homeLayoutBarLogInButton.isEnabled = false

                        clearAppBarDivider()
                        homeLayoutProgressBar.visibility = View.VISIBLE
                        homeLayoutRecyclerView.visibility = View.GONE
                    }
                })

            vm.logInSnackbarEvent.observe(viewLifecycleOwner, Observer {
                showLogInSnackbar(it)
            })

            vm.errorEvent.observe(viewLifecycleOwner, Observer { isNetworkError->
                if (isNetworkError) {
                    addFragment(InternetIsDisabledMainFragment(), R.id.homeLayoutFrameLayout, false)
                }else{
                    addFragment(ServerProblemsMainFragment(), R.id.homeLayoutFrameLayout, false)
                }
            })

            UserUtils.userDataLiveData.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { data ->

                    if(vm.results != null && vm.progressState.value == false)
                        vm.progressState.value = true

                    if (UserUtils.isLoggedIn() && data != null) {
                        homeLayoutBarAccountNotificationsIndicator.asNotificationIndicator(data.notificationsAreChecked)
                        homeLayoutBarAccountImageView.fillAvatar(data.avatarLink, data.userName!!, data.userId!!)
                    }
                })

            vm.subscribeEmailState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { status ->
                    showSnackbar(status, view.context)
                })


            vm.subscribeErrorEvent.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer {
                    CustomSnackbar(context!!).show(
                        resources.getString(R.string.server_temporarily_unavailable),
                        ThemeUtils.getRedColor(context!!),
                        bottomMarginExtra = resources.getDimension(R.dimen.bottomNavigationHeight)
                            .toInt()
                    )
                })

            if (UserUtils.isLoggedIn()) {
                homeLayoutBarLogInButton.visibility = View.GONE
                homeLayoutBarAccountImageLayout.visibility = View.VISIBLE
            }
        }
        homeLayoutBarLogInButton.setOnClickListener { (activity as AppCompatActivity).startActivity<MainMenuActivity>() }
        homeLayoutBarAccountImageLayout.setOnClickListener { (activity as AppCompatActivity).startActivity<MainMenuActivity>() }
        homeLayoutAppBar.addOnOffsetChangedListener(this)

        OneShotPreDrawListener.add(homeLayoutAppBar){
            removeAppBarDrag()
        }

        mainFragmentViewModel?.showFab(true)
        homeLayoutRecyclerView.addOnScrollListener(FabRecyclerImpl(this))
    }

    override fun onResume() {
        super.onResume()

        if(PrefsUtils.isCompletedChapterDialogWasNotShown()){
            homeFragmentViewModel?.viewModelScope?.launch (Dispatchers.Main){
                delay(500L)
                val dialogData = PrefsUtils.getCompletedChapterDialogData()
                activity?.supportFragmentManager?.let {
                    ChapterCompletedDialogAlertFragment.getInstance(
                        dialogData.first,
                        dialogData.second
                    ).show(
                        it, null
                    )
                    PrefsUtils.makeCompletedChapterDialogWasShown()
                }
            }
        }
    }

    override fun onScroll(
        scrollY: Int
    ) {
        homeLayoutBar.background =
            if (scrollY != 0) context?.getDrawable(R.drawable.divider_bottom_drawable) else null
    }

    private fun clearAppBarDivider(){
        homeLayoutBar.background = null
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


    private fun removeAppBarDrag(){
        val behavior = (homeLayoutAppBar.layoutParams as CoordinatorLayout.LayoutParams).behavior as AppBarLayout.Behavior?
        behavior!!.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
    }

    private fun getLevels(vm: HomeFragmentViewModel){
        vm.clearResults()
        vm.progressState.value = false
        vm.getLevels()
    }

    private fun showSnackbar(status: SubscribeEmailResultStatus, context: Context) {
        val snackbar = CustomSnackbar(context)
        when (status) {
            is SubscribeEmailResultStatus.Success -> {
                if (status.result.subscribe) {
                    snackbar.show(
                        resources.getString(
                            R.string.subscribe_email,
                            TextUtils.encryptEmail(status.result.email!!)
                        ),
                        ThemeUtils.getAccentColor(snackbar.context),
                        resources.getInteger(R.integer.subscribe_email_snackbar_duration)
                            .toLong(),
                        bottomMarginExtra = resources.getDimension(R.dimen.bottomNavigationHeight)
                            .toInt()
                    )
                } else {
                    snackbar.show(
                        resources.getString(R.string.unsubscribe_email),
                        ThemeUtils.getAccentColor(snackbar.context),
                        resources.getInteger(R.integer.unsubscribe_email_snackbar_duration)
                            .toLong(),
                        bottomMarginExtra = resources.getDimension(R.dimen.bottomNavigationHeight)
                            .toInt()
                    )
                }
            }
            is SubscribeEmailResultStatus.NoConnection -> {
                if (!UserUtils.userData.isSubscribed) {
                    snackbar.show(
                        resources.getString(R.string.offline_subscribe_email),
                        ThemeUtils.getAccentColor(snackbar.context),
                        resources.getInteger(R.integer.offline_subscribe_email_snackbar_duration)
                            .toLong(),
                        bottomMarginExtra = resources.getDimension(R.dimen.bottomNavigationHeight)
                            .toInt()
                    )
                } else {
                    snackbar.show(
                        resources.getString(R.string.offline_unsubscribe_email),
                        ThemeUtils.getAccentColor(snackbar.context),
                        resources.getInteger(R.integer.offline_subscribe_email_snackbar_duration)
                            .toLong(),
                        bottomMarginExtra = resources.getDimension(R.dimen.bottomNavigationHeight)
                            .toInt()
                    )
                }
            }
        }
    }

    private fun showLogInSnackbar(isAfterSignUp: Boolean){
        val snackbar = CustomSnackbar(context!!)
            snackbar.show(
                if(isAfterSignUp) resources.getString(R.string.welcome) else resources.getString(R.string.welcome_back),
                ThemeUtils.getGreenColor(context!!),
                resources.getInteger(R.integer.log_in_snackbar_duration)
                .toLong(),
            bottomMarginExtra = resources.getDimension(R.dimen.bottomNavigationHeight)
                .toInt()
        )
    }

    override fun onPageReload() {
        if(App.authenticationState.value?.first !is CheckTokenIsCorrectStatus.Correct)
            App.authenticate(UserUtils.getUserIDTokenData(), false)

        if(UserUtils.userDataIsNull())
            App.getUserData()

        if(!(PrefsUtils.isOfflineModeEnabled() && homeFragmentViewModel?.results?.isNotEmpty() == true))
            homeFragmentViewModel?.getLevels()
    }
}
