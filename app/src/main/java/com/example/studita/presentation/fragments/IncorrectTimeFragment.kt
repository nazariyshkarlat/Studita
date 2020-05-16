package com.example.studita.presentation.fragments

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
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.utils.TimeUtils
import com.example.studita.utils.replace
import kotlinx.android.synthetic.main.incorrect_time_layout.*
import java.util.*


class IncorrectTimeFragment : BaseFragment(R.layout.incorrect_time_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateTime = TimeUtils.IncorrectTimeDateTimeFormat().format(Date())
        incorrectTimeLayoutTitle.text = resources.getString(R.string.incorrect_time_layout_title, dateTime)
        incorrectTimeLayoutSubtitle.text = resources.getString(R.string.incorrect_time_layout_subtitle)

        incorrectTimeLayoutInSettingsButton.setOnClickListener {
            startActivityForResult(Intent(Settings.ACTION_DATE_SETTINGS), 0)
        }
        incorrectTimeLayoutTryAgainButton.setOnClickListener {

            animateRefreshButton(it as TextView)
            checkIfTimeIsAutomatically(it.context)
        }
    }

    override fun onResume() {
        super.onResume()
        context?.let { checkIfTimeIsAutomatically(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        context?.let { checkIfTimeIsAutomatically(it) }
    }

    private fun checkIfTimeIsAutomatically(context: Context){
        if(TimeUtils.timeIsAutomatically(context))
            (activity as AppCompatActivity).replace(MainFragment(), R.id.frameLayout)
    }

    private fun animateRefreshButton(textView: TextView){
        val refreshDrawable = (textView.compoundDrawables[0] as RotateDrawable)

        val anim = ObjectAnimator.ofPropertyValuesHolder(
            refreshDrawable,
            PropertyValuesHolder.ofInt("level", 0, 10000)
        ).setDuration(resources.getInteger(R.integer.refresh_animation_time).toLong())

        anim.interpolator = FastOutSlowInInterpolator()
        anim.setAutoCancel(true)
        anim.start()
    }
}