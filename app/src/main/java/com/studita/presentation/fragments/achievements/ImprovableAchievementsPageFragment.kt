package com.studita.presentation.fragments.achievements

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.studita.R
import com.studita.presentation.activities.MainActivity
import com.studita.presentation.adapter.achievements.AchievementViewHolder
import com.studita.presentation.adapter.achievements.AchievementsAdapter
import com.studita.presentation.adapter.achievements.ImprovableAchievementViewHolder
import com.studita.presentation.model.AchievementsUiState
import com.studita.presentation.view_model.AchievementsViewModel
import com.studita.presentation.views.SpeedyLinearLayoutManager
import com.studita.utils.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ImprovableAchievementsPageFragment : AchievementsPageFragment() {
    override val isImprovable: Boolean
        get() = true
}