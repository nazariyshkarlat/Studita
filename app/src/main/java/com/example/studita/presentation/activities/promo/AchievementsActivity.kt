package com.example.studita.presentation.activities.promo

import android.os.Bundle
import com.example.studita.R
import com.example.studita.presentation.activities.DefaultActivity
import com.example.studita.presentation.fragments.promo_fragments.AchievementsPromoFragment
import com.example.studita.utils.addFragment

class AchievementsActivity : DefaultActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        if(savedInstanceState == null)
            addFragment(AchievementsPromoFragment(), R.id.frameLayout)
    }

    override fun onBackPressed() {
        finish()
    }

}