package com.studita.di

import com.studita.presentation.view_model.AchievementsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.module.Module
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val achievementsViewModel : Module = module {
    viewModel { (userId: Int?) -> AchievementsViewModel(userId, GlobalContext.get().get()) }
}