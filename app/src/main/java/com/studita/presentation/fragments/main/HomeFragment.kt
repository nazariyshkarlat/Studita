package com.studita.presentation.fragments.main

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils.compositeColors
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import com.studita.App
import com.studita.R
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
import com.studita.data.net.connection.ConnectionManager
import com.studita.presentation.fragments.main.MainFragment.Companion.getBottomMarginExtraSnackbar
import com.studita.presentation.model.toHomeRecyclerItems
import kotlinx.android.synthetic.main.home_layout.*
import kotlinx.android.synthetic.main.home_layout_bar.*
import kotlinx.android.synthetic.main.recyclerview_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext.get

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

        homeLayoutRecyclerView.isSaveEnabled = false

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
                            if(vm.errorEvent.value != ErrorState.SERVER_ERROR)
                                vm.errorEvent.value = ErrorState.SERVER_ERROR
                        }
                        is CheckTokenIsCorrectStatus.NoConnection -> {
                            if(vm.errorEvent.value != ErrorState.CONNECTION_ERROR)
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
                            it.userId!!,
                            withPlaceholder = false
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
                                ArrayList(vm.getRecyclerItems(
                                    HomeRecyclerUiModel.HomeUserDataUiModel,
                                    vm.results!!.map { it.toHomeRecyclerItems() }.flatten(),
                                )),
                                homeFragmentViewModel!!,
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
                        homeLayoutBarAccountImageView.fillAvatar(data.avatarLink, data.userName!!, data.userId!!,
                            withPlaceholder = false
                        )
                    }
                })

            vm.subscribeEmailState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { status ->
                    showSubscribeEmailSnackbar(status, view.context, true)
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

        homeLayoutBarTitle.setOnClickListener {
            activity?.supportFragmentManager?.addFragment(SelectCourseFragment(), R.id.frameLayout)
        }
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
                PrefsUtils.getCompletedChapterDialogData()?.let {pair->
                    activity?.supportFragmentManager?.let {
                        ChapterCompletedDialogAlertFragment.getInstance(
                            pair.first,
                            pair.second
                        ).show(
                            it, null
                        )
                        PrefsUtils.makeCompletedChapterDialogWasShown()
                    }
                }
            }
        }
    }

    override fun onScroll(
        scrollY: Int
    ) {
        homeLayoutAppBar.background =
            if (scrollY != 0) ContextCompat.getDrawable(context!!, R.drawable.divider_bottom_drawable) else null
    }

    private fun clearAppBarDivider(){
        homeLayoutAppBar.background = null
    }

    override fun show() {
        mainFragmentViewModel?.showFab(true)
    }

    override fun hide() {
        mainFragmentViewModel?.showFab(false)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if(verticalOffset == 0){
            mainFragmentViewModel?.showFab(true)
        }

        homeLayoutBar.alpha =
            (homeLayoutBar.height + verticalOffset * 2) / homeLayoutBar.height.toFloat()
    }


    private fun removeAppBarDrag(){
        val behavior = (homeLayoutAppBar.layoutParams as CoordinatorLayout.LayoutParams).behavior as AppBarLayout.Behavior?
        behavior?.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
    }

    private fun showLogInSnackbar(isAfterSignUp: Boolean){
        val snackbar = CustomSnackbar(context!!)
            snackbar.show(
                if(isAfterSignUp) resources.getString(R.string.welcome) else resources.getString(R.string.welcome_back),
                compositeColors(ThemeUtils.getAccentLiteColor(snackbar.context), ContextCompat.getColor(snackbar.context, R.color.white)),
                ContextCompat.getColor(snackbar.context, R.color.black),
                resources.getInteger(R.integer.log_in_snackbar_duration)
                .toLong(),
                bottomMarginExtra = getBottomMarginExtraSnackbar(context!!)
        )
    }

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
