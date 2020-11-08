package com.studita.presentation.activities.promo

import android.os.Bundle
import android.view.KeyEvent
import com.studita.R
import com.studita.presentation.activities.DefaultActivity
import com.studita.presentation.fragments.promo_fragments.CompetitionsPromoFragment
import com.studita.utils.addFragment

class CompetitionsActivity : DefaultActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        if(savedInstanceState == null)
            addFragment(CompetitionsPromoFragment(), R.id.frameLayout)
    }



}