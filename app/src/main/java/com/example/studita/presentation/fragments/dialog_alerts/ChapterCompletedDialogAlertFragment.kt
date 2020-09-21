package com.example.studita.presentation.fragments.dialog_alerts

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.view.OneShotPreDrawListener
import androidx.core.view.updateLayoutParams
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import kotlinx.android.synthetic.main.chapter_completed_dialog_alert.*
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size


class ChapterCompletedDialogAlertFragment : BaseDialogFragment(R.layout.chapter_completed_dialog_alert){

    companion object{

        fun getInstance(exercisesCount: Int, chapterName: String) = ChapterCompletedDialogAlertFragment().apply {
            arguments = bundleOf("EXERCISES_COUNT" to exercisesCount, "CHAPTER_NAME" to chapterName)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chapterCompletedDialogAlertCloseButton.setOnClickListener {
            dismiss()
        }

        arguments?.let {
            val exercisesCount = it.getInt("EXERCISES_COUNT")
            val chapterName = it.getString("CHAPTER_NAME")

            chapterCompletedDialogAlertSubtitle.text = resources.getString(
                R.string.chapter_completed_dialog_alert_subtitle_template,
                chapterName
            )

            chapterCompletedDialogAlertHundredsCell.text = (exercisesCount/100).toString()
            chapterCompletedDialogAlertTensCell.text = (exercisesCount%100/10).toString()
            chapterCompletedDialogAlertOnesCell.text = (exercisesCount%10).toString()
        }
    }

    override fun onStart() {
        super.onStart()

        view?.let {
            OneShotPreDrawListener.add(it) {
                val konfettiView = KonfettiView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(it.measuredWidth, it.measuredHeight)
                }
                (it as ViewGroup).addView(konfettiView)

                konfettiView.build()
                    .addColors(
                        Color.parseColor("#F9B04C"),
                        Color.parseColor("#E9496D"),
                        Color.parseColor("#3266D3"),
                        Color.parseColor("#F8F74A")
                    )
                    .setSpeed(1f, 5f)
                    .setDirection(0.0, 359.0)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(5000L)
                    .addShapes(Shape.Square)
                    .addSizes(Size(4))
                    .setPosition(-20F, it.measuredWidth.toFloat() + 20F, -50F, -50F)
                    .streamFor(200, 5000L)
            }
        }
    }

}