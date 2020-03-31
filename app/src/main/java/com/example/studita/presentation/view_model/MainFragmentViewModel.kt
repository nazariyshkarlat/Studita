package com.example.studita.presentation.view_model

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.studita.di.LevelsModule
import com.example.studita.di.UserDataModule
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.presentation.draw.AvaDrawer
import com.example.studita.presentation.fragments.HomeFragment
import com.example.studita.presentation.utils.PrefsUtils
import com.example.studita.presentation.utils.launchExt
import kotlinx.android.synthetic.main.home_layout_bar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragmentViewModel : ViewModel(){

    val fabState = MutableLiveData<Boolean>()
    val progressState = MutableLiveData<Boolean>(true)

    fun showFab(show: Boolean){
        fabState.value = show
    }

    fun showProgress(show: Boolean){
        progressState .value = show
    }

}