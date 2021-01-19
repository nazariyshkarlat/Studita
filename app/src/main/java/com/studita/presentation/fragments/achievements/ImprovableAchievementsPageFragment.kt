package com.studita.presentation.fragments.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studita.R
import com.studita.presentation.adapter.achievements.AchievementsAdapter
import com.studita.presentation.model.AchievementsUiState
import com.studita.utils.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class ImprovableAchievementsPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return RecyclerView(container!!.context).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT)
            clipToPadding = false
            id = R.id.improvableAchievementsRecyclerView
            updatePadding(bottom = resources.getDimension(R.dimen.bottomNavigationHeight).toInt()+8F.dp)
            isSaveEnabled = false
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    (parentFragment as AchievementsFragment).achievementsViewModel.updateDividerState(recyclerView.computeVerticalScrollOffset())
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            (parentFragment as AchievementsFragment).achievementsViewModel.improvableAchievementsState.collect {
                (view as RecyclerView).adapter = AchievementsAdapter(it, true)
            }
        }

        lifecycleScope.launchWhenCreated {
            (parentFragment as AchievementsFragment).achievementsViewModel.scrollRecyclersToTopEvent.collect {
                (view as RecyclerView).scrollToPosition(0)
                (parentFragment as AchievementsFragment).achievementsViewModel.updateDividerState(0)
            }
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