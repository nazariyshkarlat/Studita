package com.example.studita.presentation.view_model

import android.view.MenuItem
import androidx.lifecycle.ViewModel
import com.example.studita.R
import com.example.studita.presentation.fragments.AchievementsFragment
import com.example.studita.presentation.fragments.CompetitionsFragment
import com.example.studita.presentation.fragments.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivityNavigationViewModel : ViewModel(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    val navigationSelectedIdState = SingleLiveEvent<Int>()
    val navigationState = SingleLiveEvent<Pair<BottomNavigationEnum, String>>()

    private val backStack: ArrayList<Pair<Int, String>> =
        arrayListOf(R.id.bottomNavigationFirstItem to HomeFragment::class.java.name)

    init {
        navigationState.value = BottomNavigationEnum.ADD to HomeFragment::class.java.name
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val lastId = backStack.last().first
        if (item.itemId != lastId) {
            val fragmentTag: String
            navigationState.value = BottomNavigationEnum.HIDE to backStack.last().second

            if (backStack.map { it.first }.contains(item.itemId)) {
                val pairToRemove = backStack[backStack.map { it.first }.indexOf(item.itemId)]
                fragmentTag = pairToRemove.second
                navigationState.value = BottomNavigationEnum.SHOW to fragmentTag
                backStack.remove(pairToRemove)
            } else {
                fragmentTag = getFragmentToAdd(item.itemId)
                navigationState.value = BottomNavigationEnum.ADD to fragmentTag
            }
            backStack.add(item.itemId to fragmentTag)
        }
        return true
    }

    private fun getFragmentToAdd(itemId: Int): String {
        return when (itemId) {
            R.id.bottomNavigationFirstItem -> HomeFragment::class.java.name
            R.id.bottomNavigationSecondItem -> CompetitionsFragment::class.java.name
            R.id.bottomNavigationThirdItem -> AchievementsFragment::class.java.name
            else -> throw UnsupportedOperationException("unknown item id")
        }
    }

    fun onBackPressed() {
        if (backStack.size != 1) {
            val toRemovePair = backStack.last()
            navigationState.value = BottomNavigationEnum.REMOVE to toRemovePair.second
            backStack.removeAt(backStack.lastIndex)
            val toSelectPair = backStack.last()
            navigationState.value = BottomNavigationEnum.SHOW to toSelectPair.second
            navigationSelectedIdState.value = toSelectPair.first
        } else
            navigationState.value = BottomNavigationEnum.CLOSE_APP to "CLOSE_APP"
    }

    enum class BottomNavigationEnum {
        ADD,
        REMOVE,
        SHOW,
        HIDE,
        CLOSE_APP
    }

}