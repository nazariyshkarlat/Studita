package com.example.studita.presentation.fragments.dialog_alerts

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import kotlinx.android.synthetic.main.chapter_completed_dialog_alert.*
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

            chapterCompletedDialogAlertSubtitle.text = resources.getString(R.string.chapter_completed_dialog_alert_subtitle_template, chapterName)

            chapterCompletedDialogAlertHundredsCell.text = (exercisesCount/100).toString()
            chapterCompletedDialogAlertTensCell.text = (exercisesCount%100/10).toString()
            chapterCompletedDialogAlertOnesCell.text = (exercisesCount%10).toString()
        }
    }

    override fun onStart() {
        super.onStart()
        chapterCompletedDialogAlertKonfettiView.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.Square, Shape.Circle)
            .addSizes(Size(12))
            .setPosition(-50f, chapterCompletedDialogAlertKonfettiView.width + 50f, -50f, -50f)
            .streamFor(300, 5000L)
    }

}