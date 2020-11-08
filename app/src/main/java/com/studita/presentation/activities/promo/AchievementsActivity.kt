package com.studita.presentation.activities.promo

import android.os.Bundle
import android.view.KeyEvent
import com.studita.R
import com.studita.presentation.activities.DefaultActivity
import com.studita.presentation.fragments.promo_fragments.AchievementsPromoFragment
import com.studita.utils.addFragment

class AchievementsActivity : DefaultActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        if(savedInstanceState == null)
            addFragment(AchievementsPromoFragment(), R.id.frameLayout)
    }

}