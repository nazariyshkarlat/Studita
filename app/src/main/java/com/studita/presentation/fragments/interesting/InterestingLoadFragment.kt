package com.studita.presentation.fragments.interesting

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.exercises.LoadFragment
import com.studita.presentation.view_model.InterestingViewModel
import com.studita.utils.replace

class InterestingLoadFragment : LoadFragment() {

    private var interestingViewModel: InterestingViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        interestingViewModel = activity?.run {
            ViewModelProviders.of(this).get(InterestingViewModel::class.java)
        }

        interestingViewModel?.let {


            it.interestingDataReceivedEvent.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> { done ->
                    if (done) {
                        (activity as AppCompatActivity).replace(
                            InterestingFragment(),
                            R.id.frameLayout,
                            0,
                            android.R.animator.fade_out,
                            addToBackStack = false
                        )
                    }
                })

            it.loadScreenBadConnectionState.observe(viewLifecycleOwner, Observer { done->
                if(done)
                    formBadConnectionButton{it.getInteresting()}
            })

        }

    }

}