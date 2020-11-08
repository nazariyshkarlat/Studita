package com.studita.presentation.view_model

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.studita.R
import com.studita.presentation.activities.promo.AchievementsActivity
import com.studita.presentation.activities.promo.CompetitionsActivity
import com.studita.presentation.fragments.main.HomeFragment
import com.studita.presentation.fragments.promo_fragments.AchievementsPromoFragment
import com.studita.presentation.fragments.promo_fragments.CompetitionsPromoFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivityNavigationViewModel : ViewModel(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    val navigationSelectedIdState = SingleLiveEvent<Int>()
    val navigationState = SingleLiveEvent<Pair<BottomNavigationEnum, String>>()

    val startActivityState = SingleLiveEvent<Class<out AppCompatActivity>>()

    private val backStack: ArrayList<Pair<Int, String>> =
        arrayListOf(R.id.bottomNavigationFirstItem to HomeFragment::class.java.name)

    init {
        navigationState.value = BottomNavigationEnum.ADD to HomeFragment::class.java.name
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
/*        val lastId = backStack.last().first
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
        }*/

        if(item.itemId == R.id.bottomNavigationSecondItem){
            startActivityState.value = CompetitionsActivity::class.java
        }else if(item.itemId == R.id.bottomNavigationThirdItem){
            startActivityState.value = AchievementsActivity::class.java
        }

        return item.itemId == R.id.bottomNavigationFirstItem
    }

    private fun getFragmentToAdd(itemId: Int): String {
        return when (itemId) {
            R.id.bottomNavigationFirstItem -> HomeFragment::class.java.name
            R.id.bottomNavigationSecondItem -> CompetitionsPromoFragment::class.java.name
            R.id.bottomNavigationThirdItem -> AchievementsPromoFragment::class.java.name
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