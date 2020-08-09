package com.example.studita.presentation.fragments.interesting

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.LoadFragment
import com.example.studita.presentation.view_model.InterestingViewModel
import com.example.studita.utils.replace

class InterestingLoadFragment : LoadFragment() {

    private var interestingViewModel: InterestingViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        interestingViewModel = activity?.run {
            ViewModelProviders.of(this).get(InterestingViewModel::class.java)
        }

        interestingViewModel?.let {
            it.interestingState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> { done ->
                    if (done) {
                        (activity as AppCompatActivity).replace(
                            InterestingFragment(),
                            R.id.frameLayout,
                            0,
                            android.R.animator.fade_out
                        )
                    }
                })

            it.errorState.observe(viewLifecycleOwner, Observer { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            })
        }

    }

}