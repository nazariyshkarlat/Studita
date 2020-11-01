package com.example.studita.presentation.activities.promo

import android.os.Bundle
import com.example.studita.R
import com.example.studita.presentation.activities.DefaultActivity
import com.example.studita.presentation.fragments.promo_fragments.CompetitionsPromoFragment
import com.example.studita.utils.addFragment

class CompetitionsActivity : DefaultActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        if(savedInstanceState == null)
            addFragment(CompetitionsPromoFragment(), R.id.frameLayout)
    }

    override fun onBackPressed() {
        finish()
    }

}