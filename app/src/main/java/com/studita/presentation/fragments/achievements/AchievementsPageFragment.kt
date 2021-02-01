package com.studita.presentation.fragments.achievements

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studita.R
import com.studita.presentation.activities.MainActivity
import com.studita.presentation.adapter.achievements.AchievementsAdapter
import com.studita.presentation.model.AchievementsUiState
import com.studita.presentation.view_model.AchievementsViewModel
import com.studita.presentation.views.SpeedyLinearLayoutManager
import com.studita.utils.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
abstract class AchievementsPageFragment : Fragment() {

    abstract val isImprovable: Boolean

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return RecyclerView(container!!.context).apply {
            layoutManager = SpeedyLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT
            )
            clipToPadding = false
            id = if(isImprovable) R.id.improvableAchievementsRecyclerView else R.id.nonImprovableAchievementsRecyclerView
            updatePadding(
                bottom = (if (activity is MainActivity) resources.getDimension(R.dimen.bottomNavigationHeight)
                    .toInt() else 0) + 8F.dp
            )
            isSaveEnabled = false
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    (parentFragment as AchievementsFragment).achievementsViewModel.updateDividerState(
                        recyclerView.computeVerticalScrollOffset()
                    )
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            (parentFragment as AchievementsFragment).achievementsViewModel.let{
                if(isImprovable) it.improvableAchievementsState else it.nonImprovableAchievementsState}
                .collect {
                    (view as RecyclerView).adapter = AchievementsAdapter(it, isImprovable)
                }
        }

        lifecycleScope.launchWhenCreated {
            (parentFragment as AchievementsFragment).achievementsViewModel.scrollRecyclersToTopEvent.collect {
                (view as RecyclerView).scrollToPosition(0)
                (parentFragment as AchievementsFragment).achievementsViewModel.updateDividerState(0)
            }
        }

        lifecycleScope.launchWhenStarted {
            (parentFragment as AchievementsFragment).achievementsViewModel.recyclerViewScrollToItemEvent.collect {
                if(it.first == (if(isImprovable) AchievementsViewModel.AchievementsCategory.IMPROVABLE
                    else AchievementsViewModel.AchievementsCategory.NON_IMPROVABLE)) {
                            (view as RecyclerView).smoothScrollToPosition(it.second)
                        delay(200)
                        if(view.scrollState == RecyclerView.SCROLL_STATE_IDLE)
                            animateRecyclerViewItem(it.second)
                        else
                            addRecyclerViewItemAnimation(it.second)
                }
            }
        }
    }

    private fun addRecyclerViewItemAnimation(position: Int) {
        (view as RecyclerView).addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        animateRecyclerViewItem(position)
                        recyclerView.removeOnScrollListener(this)
                    }
                }
            }
        })
    }

    private fun animateRecyclerViewItem(position: Int){
        (((view as RecyclerView).layoutManager as LinearLayoutManager).findViewByPosition(position)?.background as? TransitionDrawable)?.startTransition(500)

        lifecycleScope.launch {
            delay(3000)

            (((view as RecyclerView).layoutManager as LinearLayoutManager).findViewByPosition(position)?.background as? TransitionDrawable)?.reverseTransition(500)
        }
    }

    override fun onResume() {
        super.onResume()
        if((parentFragment as AchievementsFragment).achievementsViewModel.uiState.value == AchievementsUiState.AchievementsReceived) {
            (view as? RecyclerView)?.let {
                (parentFragment as AchievementsFragment).achievementsViewModel.updateDividerState(
                    it.computeVerticalScrollOffset()
                )
            }
        }
    }
}