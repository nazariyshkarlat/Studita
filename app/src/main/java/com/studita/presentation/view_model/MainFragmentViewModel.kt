package com.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.domain.interactor.GetAchievementsStatus
import com.studita.domain.interactor.achievements.AchievementsInteractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class MainFragmentViewModel : ViewModel() {

    val fabState = MutableLiveData<Boolean>()

    fun showFab(show: Boolean) {
        fabState.value = show
    }


}