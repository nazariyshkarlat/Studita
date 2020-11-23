package com.studita.presentation.fragments.main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import com.studita.App
import com.studita.R
import com.studita.di.NetworkModule
import com.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.studita.domain.interactor.SubscribeEmailResultStatus
import com.studita.presentation.activities.MainMenuActivity
import com.studita.presentation.adapter.levels.LevelsAdapter
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.fragments.dialog_alerts.ChapterCompletedDialogAlertFragment
import com.studita.presentation.fragments.error_fragments.InternetIsDisabledMainFragment
import com.studita.presentation.fragments.error_fragments.ServerProblemsMainFragment
import com.studita.presentation.listeners.FabRecyclerImpl
import com.studita.presentation.listeners.FabScrollListener
import com.studita.presentation.listeners.ReloadPageCallback
import com.studita.presentation.model.ErrorState
import com.studita.presentation.model.HomeRecyclerUiModel
import com.studita.presentation.view_model.HomeFragmentViewModel
import com.studita.presentation.view_model.MainFragmentViewModel
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.*
import com.studita.utils.UserUtils.observeNoNull
import com.google.android.material.appbar.AppBarLayout
import com.studita.App.Companion.authenticate
import com.studita.App.Companion.authenticationState
import com.studita.App.Companion.getUserData
import com.studita.App.Companion.offlineModeChangeEvent
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

            offlineModeChangeEvent.observe(viewLifecycleOwner, Observer { isEnabled->

                if(!isEnabled)
                    homeFragmentViewModel?.clearResults()
                else {
                    vm.errorEvent.value = ErrorState.NO_ERROR
                    vm.levelsJob?.cancel()
                }

            })


            authenticationState.observe(viewLifecycleOwner, Observer { pair->
                val checkTokenIsCorrect = pair.first
                val isOfflineModeChanged = pair.second

                if (isOfflineModeChanged) {

                    when (checkTokenIsCorrect) {
                        is CheckTokenIsCorrectStatus.Waiting -> {
                            if(!(PrefsUtils.isOfflineModeEnabled() && vm.results?.isNotEmpty() == true)) {
                                vm.getLevels()
                            }
                        }
                        is CheckTokenIsCorrectStatus.Correct -> {
                            if(PrefsUtils.isOfflineModeEnabled()){
                                if(vm.results.isNullOrEmpty()) {
                                    vm.getLevels()
                                }else {
                                    vm.errorEvent.value = ErrorState.NO_ERROR
                                    homeFragmentViewModel?.progressState?.value = false
                                }
                            }else{
                                if(vm.levelsJob?.isActive == false && vm.results.isNullOrEmpty()){
                                    vm.getLevels()
                                }else{
                                    homeFragmentViewModel?.progressState?.value = true
                                }
                            }
                        }
                        is CheckTokenIsCorrectStatus.ServiceUnavailable, CheckTokenIsCorrectStatus.Failure -> {
                           vm.errorEvent.value = ErrorState.SERVER_ERROR
                        }
                        is CheckTokenIsCorrectStatus.NoConnection -> {
                            vm.errorEvent.value = ErrorState.CONNECTION_ERROR
                        }

                    }

                    authenticationState.value = checkTokenIsCorrect to false
                }
            })

            if(UserUtils.userDataIsNull()) {
                UserUtils.localUserDataLiveData.observeNoNull(viewLifecycleOwner, Observer {

                    if(UserUtils.isLoggedIn()) {
                        homeLayoutBarAccountImageView.fillAvatar(
                            it.avatarLink,
                            it.userName!!,
                            it.userId!!
                        )
                    }
                })
            }

            vm.progressState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { isProgress ->
                    if (!isProgress) {
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

                        if(vm.errorEvent.value == ErrorState.NO_ERROR)
                            showSnackbars()
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

            vm.errorEvent.observe(viewLifecycleOwner, Observer { errorState->
                val currentFragment = childFragmentManager.findFragmentById(R.id.homeLayoutFrameLayout)
                when (errorState) {
                    ErrorState.CONNECTION_ERROR-> {
                        if(!PrefsUtils.isOfflineModeEnabled() && currentFragment !is InternetIsDisabledMainFragment) {
                            if(currentFragment is ServerProblemsMainFragment){
                                replace(
                                    InternetIsDisabledMainFragment(),
                                    R.id.homeLayoutFrameLayout,
                                    addToBackStack = false
                                )
                            }else {
                                addFragment(
                                    InternetIsDisabledMainFragment(),
                                    R.id.homeLayoutFrameLayout,
                                    false
                                )
                            }
                        }
                    }
                    ErrorState.SERVER_ERROR -> {
                        if(!PrefsUtils.isOfflineModeEnabled() && currentFragment !is ServerProblemsMainFragment) {
                            if(currentFragment is InternetIsDisabledMainFragment){
                                replace(
                                    ServerProblemsMainFragment(),
                                    R.id.homeLayoutFrameLayout,
                                    addToBackStack = false
                                )
                            }else {
                                addFragment(
                                    ServerProblemsMainFragment(),
                                    R.id.homeLayoutFrameLayout,
                                    false
                                )
                            }
                        }
                    }
                    ErrorState.NO_ERROR -> {
                        if(currentFragment is InternetIsDisabledMainFragment ||
                            currentFragment is ServerProblemsMainFragment){
                            childFragmentManager.removeFragment(
                                currentFragment
                            )
                        }

                        showSnackbars()
                    }
                    else -> {}
                }
            })

            UserUtils.userDataLiveData.observeNoNull(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { data ->
                    if(!vm.results.isNullOrEmpty() && !(vm.resultsAreLocal && (!PrefsUtils.isOfflineModeEnabled() || UserUtils.isLoggedIn()))) {
                        vm.errorEvent.value = ErrorState.NO_ERROR

                        if(vm.progressState.value == true)
                            vm.progressState.value = false
                    }

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

        mainFragmentViewModel?.showFab(true)
        homeLayoutRecyclerView.addOnScrollListener(FabRecyclerImpl(this))
    }

    private fun showSnackbars(){
        if (UserUtils.isLoggedIn())
            homeFragmentViewModel?.initSubscribeEmailState()

        if (arguments?.containsKey("IS_AFTER_SIGN_UP") == true) {
            homeFragmentViewModel?.showLogInSnackbar(arguments!!.getBoolean("IS_AFTER_SIGN_UP"))
            arguments?.clear()
        }
    }

    override fun onStart() {
        super.onStart()
        OneShotPreDrawListener.add(homeLayoutAppBar){
            removeAppBarDrag()
        }
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
        behavior?.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
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
                        bottomMarginExtra = getBottomMarginExtraSnackbar()
                    )
                } else {
                    snackbar.show(
                        resources.getString(R.string.unsubscribe_email),
                        ThemeUtils.getAccentColor(snackbar.context),
                        resources.getInteger(R.integer.unsubscribe_email_snackbar_duration)
                            .toLong(),
                        bottomMarginExtra = getBottomMarginExtraSnackbar()
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
                        bottomMarginExtra = getBottomMarginExtraSnackbar()
                    )
                } else {
                    snackbar.show(
                        resources.getString(R.string.offline_unsubscribe_email),
                        ThemeUtils.getAccentColor(snackbar.context),
                        resources.getInteger(R.integer.offline_subscribe_email_snackbar_duration)
                            .toLong(),
                        bottomMarginExtra = getBottomMarginExtraSnackbar()
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
                bottomMarginExtra = getBottomMarginExtraSnackbar()
        )
    }

    fun showEnableOfflineModeSnackbar(){
        CustomSnackbar(context!!).show(
            resources.getString(R.string.enable_offline_mode_snackbar), ThemeUtils.getAccentColor(context!!),
            bottomMarginExtra = getBottomMarginExtraSnackbar()
        )
    }

    private fun getBottomMarginExtraSnackbar() = resources.getDimension(R.dimen.bottomNavigationHeight).toInt() +
            if(NetworkModule.connectionManager.isNetworkAbsent())  resources.getDimension(R.dimen.connectionSnackbarHeight).toInt()
            else 0

    override fun onPageReload() {

        var hideProgress = true

        if(authenticationState.value?.first !is CheckTokenIsCorrectStatus.Correct) {
            authenticate(UserUtils.getUserIDTokenData(), false)
            hideProgress = false
        }else{
            if(UserUtils.userDataIsNull()) {
                getUserData()
                hideProgress = false
            }
        }

        if(!(PrefsUtils.isOfflineModeEnabled() && homeFragmentViewModel?.results?.isNotEmpty() == true)) {
            homeFragmentViewModel?.getLevels()
            hideProgress = false
        }

        if(hideProgress && homeFragmentViewModel?.progressState?.value == true){
            homeFragmentViewModel?.progressState?.value = false
        }

    }
}
