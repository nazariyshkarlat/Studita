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
class NonImprovableAchievementsPageFragment : AchievementsPageFragment() {
    override val isImprovable: Boolean
        get() = false
}