package com.studita.presentation.fragments.interesting

import android.os.Bundle
import android.view.View
import com.studita.R
import com.studita.presentation.audio.SoundPoolPlayer
import com.studita.presentation.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.interesting_specific_drum_roll_layout.*


class InterestingSpecificDrumRollFragment :
    BaseFragment(R.layout.interesting_specific_drum_roll_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sound = SoundPoolPlayer(view.context)
        interestingSpecificDrumRollLayoutButton.setOnClickListener {
            playDrumRoll(sound)
        }
    }

    private fun playDrumRoll(sound: SoundPoolPlayer) {
        sound.playShortResource(R.raw.drum_roll)
    }

}