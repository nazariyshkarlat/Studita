package com.studita.presentation.fragments.achievements

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.OneShotPreDrawListener
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.studita.App
import com.studita.R
import com.studita.presentation.activities.MainActivity
import com.studita.presentation.activities.MainMenuActivity
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.fragments.error_fragments.InternetIsDisabledMainFragment
import com.studita.presentation.fragments.error_fragments.MainLayoutOfflineModeErrorFragment
import com.studita.presentation.fragments.error_fragments.ServerProblemsMainFragment
import com.studita.presentation.listeners.ReloadPageCallback
import com.studita.presentation.model.AchievementsUiState
import com.studita.presentation.view_model.AchievementsViewModel
import com.studita.utils.*
import com.studita.utils.UserUtils.observeNoNull
import kotlinx.android.synthetic.main.achievements_layout.*
import kotlinx.android.synthetic.main.achievements_layout_bar.*
import kotlinx.android.synthetic.main.achievements_layout_bar.view.*
import kotlinx.android.synthetic.main.home_layout.*
import kotlinx.android.synthetic.main.home_layout_bar.*
import kotlinx.android.synthetic.main.profile_layout.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class AchievementsFragment : NavigatableFragment(R.layout.achievements_layout), AppBarLayout.OnOffsetChangedListener, ReloadPageCallback, SwipeRefreshLayout.OnRefreshListener{

    internal val achievementsViewModel: AchievementsViewModel by viewModel {
        parametersOf(PrefsUtils.getUserId())
    }

    private val achievementsLayoutBarIsVisible by lazy{  activity is MainActivity }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(activity !is MainActivity){
            achievementsLayoutBar.visibility = View.GONE
            achievementsLayoutTabs.updatePadding(top = resources.getDimension(R.dimen.toolbarHeight).toInt() + 8F.dp)
        }

        lifecycleScope.launchWhenStarted {

            achievementsViewModel.uiState.collect {

                when(it){
                    AchievementsUiState.NetworkError -> {
                        if (childFragmentManager.findFragmentById(R.id.achievementsLayoutFrameLayout) == null) {
                            addFragment(
                                InternetIsDisabledMainFragment(),
                                R.id.achievementsLayoutFrameLayout,
                                false
                            )
                        }
                        achievementsLayoutSwipeRefresh.isEnabled = false
                    }
                    AchievementsUiState.ServerError -> {
                        if (childFragmentManager.findFragmentById(R.id.achievementsLayoutFrameLayout) == null) {
                            addFragment(
                                ServerProblemsMainFragment(),
                                R.id.achievementsLayoutFrameLayout,
                                false
                            )
                        }
                        achievementsLayoutSwipeRefresh.isEnabled = false
                    }
                    AchievementsUiState.Loading -> {
                        achievementsLayoutProgressBar.visibility = View.VISIBLE
                        achievementsLayoutViewPager.visibility = View.GONE
                        achievementsLayoutTabs.visibility = View.GONE

                        OneShotPreDrawListener.add(achievementsLayoutAppBar) {
                            removeAppBarDrag()
                        }

                        checkShowLogInBlock()
                        achievementsLayoutSwipeRefresh.isEnabled = false
                    }
                    AchievementsUiState.AchievementsReceived -> {
                        achievementsLayoutProgressBar.visibility = View.GONE
                        achievementsLayoutViewPager.visibility = View.VISIBLE
                        achievementsLayoutTabs.visibility = View.VISIBLE

                        if (achievementsLayoutTabs.viewPager == null) {
                            formPages()
                            achievementsLayoutTabs.syncWithViewPager(
                                achievementsLayoutViewPager,
                                childFragmentManager
                            )
                        }

                        handleOpenFromNotifications()

                        OneShotPreDrawListener.add(achievementsLayoutAppBar) {
                            addAppBarDrag()
                        }

                        checkShowLogInBlock()
                        achievementsLayoutSwipeRefresh.isEnabled = true
                    }
                    AchievementsUiState.OfflineModeIsEnabled -> {
                        achievementsLayoutProgressBar.visibility = View.GONE
                        achievementsLayoutViewPager.visibility = View.GONE
                        achievementsLayoutTabs.visibility = View.GONE
                        achievementsLayoutBar.achievementsLayoutBarLogInBlock.visibility = View.GONE

                        OneShotPreDrawListener.add(achievementsLayoutAppBar) {
                            removeAppBarDrag()
                        }

                        if (childFragmentManager.findFragmentById(R.id.achievementsLayoutFrameLayout) == null) {
                            addFragment(
                                MainLayoutOfflineModeErrorFragment(),
                                R.id.achievementsLayoutFrameLayout,
                                false
                            )
                        }
                        achievementsLayoutSwipeRefresh.isEnabled = false
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            achievementsViewModel.recyclerDividerIsVisibleState.collect {
                achievementsLayoutAppBar.background =
                    if (it) ContextCompat.getDrawable(context!!, R.drawable.divider_bottom_drawable) else null
            }
        }

        lifecycleScope.launchWhenStarted {
            achievementsViewModel.viewPagerNavigateToPageEvent.collect {
                achievementsLayoutViewPager.currentItem = it
                println(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            achievementsViewModel.scrollRecyclersToTopEvent.collect {
                achievementsLayoutAppBar.setExpanded(true, false)
            }
        }

        lifecycleScope.launchWhenCreated {
            achievementsViewModel.uiStateUpdateEvent.collect{
                achievementsLayoutSwipeRefresh.isRefreshing = false
            }
        }

        App.offlineModeChangeEvent.observe(viewLifecycleOwner){ offlineModeWasEnabled->
            if(offlineModeWasEnabled) achievementsViewModel.onEnableOfflineMode() else achievementsViewModel.getAchievements()
        }

        UserUtils.userDataLiveData.observeNoNull(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { data ->
                if (UserUtils.isLoggedIn() && data != null) {
                    achievementsLayoutBarAccountNotificationsIndicator.asNotificationIndicator(data.notificationsAreChecked)
                    achievementsLayoutBarAccountImageView.fillAvatar(
                        data.avatarLink, data.userName!!, data.userId!!,
                        withPlaceholder = false
                    )
                }
            })

        if(UserUtils.userDataIsNull()) {
            UserUtils.localUserDataLiveData.observeNoNull(viewLifecycleOwner) {

                if(UserUtils.isLoggedIn()) {
                    achievementsLayoutBarAccountImageView.fillAvatar(
                        it.avatarLink,
                        it.userName!!,
                        it.userId!!,
                        withPlaceholder = false
                    )
                }
            }
        }

        achievementsLayoutAppBar.addOnOffsetChangedListener(this)

        if(achievementsLayoutBarIsVisible) {
            initAchievementsLayoutBar()
        }

        initRefreshLayout(view.context)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        achievementsLayoutBarToolbarBlock.alpha =
            (achievementsLayoutBarToolbarBlock.height + verticalOffset * 2) / achievementsLayoutBarToolbarBlock.height.toFloat()
    }

    private fun removeAppBarDrag(){
        val behavior = (achievementsLayoutAppBar.layoutParams as CoordinatorLayout.LayoutParams).behavior as AppBarLayout.Behavior?
        behavior?.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
    }

    private fun handleOpenFromNotifications(){
        if(arguments?.containsKey("NOTIFICATION_TYPE_SELECTION") == true) {
            achievementsViewModel.navigateToAchievement(
                Json.decodeFromString(
                    arguments!!.getString(
                        "NOTIFICATION_TYPE_SELECTION"
                    )!!
                )
            )
            arguments!!.remove("NOTIFICATION_TYPE_SELECTION")
        }
    }

    private fun addAppBarDrag(){
        val behavior = (achievementsLayoutAppBar.layoutParams as CoordinatorLayout.LayoutParams).behavior as AppBarLayout.Behavior?
        behavior?.setDragCallback(null)
    }

    private fun formPages(){
        achievementsLayoutTabs.setFragments(
            listOf(
                NonImprovableAchievementsPageFragment(),
                ImprovableAchievementsPageFragment()
            )
        )
        achievementsLayoutTabs.setItems(
            listOf(
                resources.getString(R.string.non_improvable_achievement_tab),
                resources.getString(R.string.improvable_achievement_tab)
            )
        )
    }

    private fun initRefreshLayout(context: Context) {
        achievementsLayoutSwipeRefresh.setProgressViewOffset(
            false,
            0,
            24F.dp
        )
        achievementsLayoutSwipeRefresh.setOnRefreshListener(this)
        achievementsLayoutSwipeRefresh.isEnabled = false
        achievementsLayoutSwipeRefresh.setColorSchemeColors(ContextCompat.getColor(context, R.color.black))
        achievementsLayoutSwipeRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(context, R.color.white)
        )
    }

    private fun initAchievementsLayoutBar(){
        if (UserUtils.isLoggedIn()) {
            achievementsLayoutBarLogInButton.visibility = View.GONE
            achievementsLayoutBarAccountImageLayout.visibility = View.VISIBLE
            achievementsLayoutBarAccountImageLayout.setOnClickListener { (activity as AppCompatActivity).startActivity<MainMenuActivity>() }
        } else {
            achievementsLayoutBarLogInButton.visibility = View.VISIBLE
            achievementsLayoutBarAccountImageLayout.visibility = View.GONE
            achievementsLayoutBarLogInButton.setOnClickListener { (activity as AppCompatActivity).startActivity<MainMenuActivity>() }
            achievementsLayoutBarLogInBlockButton.setOnClickListener {
                (activity as AppCompatActivity).startActivity<MainMenuActivity>()
            }
        }
    }

    private fun checkShowLogInBlock(){
        if (UserUtils.isLoggedIn()) {
            achievementsLayoutBarLogInBlock.visibility = View.GONE
        }else {
            achievementsLayoutBarLogInBlock.visibility = View.VISIBLE
        }
    }

    override fun onPageReload() {
        achievementsViewModel.getAchievements()
    }

    override fun onRefresh() {
        achievementsViewModel.getAchievements(isPageRefresh = true)
    }
}