package com.studita.presentation.activities.promo

import android.os.Bundle
import android.view.KeyEvent
import com.studita.R
import com.studita.presentation.activities.DefaultActivity
import com.studita.presentation.fragments.promo_fragments.TrainingsPromoFragment
import com.studita.utils.addFragment

class TrainingsActivity : DefaultActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        if(savedInstanceState == null)
            addFragment(TrainingsPromoFragment(), R.id.frameLayout)
    }


}