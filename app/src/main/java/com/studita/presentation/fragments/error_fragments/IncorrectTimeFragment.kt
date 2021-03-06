package com.studita.presentation.fragments.error_fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.graphics.drawable.RotateDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.studita.R
import com.studita.notifications.local.StartUpReceiver
import com.studita.presentation.activities.MainActivity
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.fragments.main.MainFragment
import com.studita.utils.TimeUtils
import com.studita.utils.animateRefreshButton
import com.studita.utils.replace
import kotlinx.android.synthetic.main.incorrect_time_layout.*
import org.w3c.dom.Text


class IncorrectTimeFragment : BaseFragment(R.layout.incorrect_time_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        incorrectTimeLayoutInSettingsButton.setOnClickListener {
            startActivityForResult(Intent(Settings.ACTION_DATE_SETTINGS), 0)
        }
        incorrectTimeLayoutTryAgainButton.setOnClickListener {

            (it as TextView).animateRefreshButton()
            checkIfTimeIsAutomatically(it.context)
        }

        view.setOnTouchListener { _, _ ->  true}
    }

    override fun onResume() {
        super.onResume()
        context?.let { checkIfTimeIsAutomatically(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        context?.let { checkIfTimeIsAutomatically(it) }
    }

    private fun checkIfTimeIsAutomatically(context: Context) {
        if (TimeUtils.timeIsAutomatically(context)) {
            (activity as AppCompatActivity).replace(
                MainActivity.getFragmentToAdd(context),
                R.id.frameLayout,
                addToBackStack = false
            )
            StartUpReceiver.scheduleLocalNotifications(context)
        }
        }
}