package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studita.presentation.fragments.HomeFragment

class HomeFragmentMainActivityFABViewModel : ViewModel(){

    val fabState = MutableLiveData<HomeFragment.FABState>()

    fun showFab(show: Boolean){
        fabState.value = if(show) HomeFragment.FABState.SHOW else HomeFragment.FABState.HIDE
    }

}