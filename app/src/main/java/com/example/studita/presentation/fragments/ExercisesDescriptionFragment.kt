package com.example.studita.presentation.fragments

import android.R.attr.x
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.OneShotPreDrawListener
import androidx.core.view.children
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExercisesDescriptionData
import com.example.studita.presentation.extensions.createSpannableString
import com.example.studita.presentation.extensions.dpToPx
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercises_description_1_layout.*
import kotlinx.android.synthetic.main.main_menu_layout.*
import java.util.regex.Pattern


class ExercisesDescriptionFragment : NavigatableFragment(R.layout.exercises_description_1_layout), ViewTreeObserver.OnScrollChangedListener{

    var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }
        exercisesViewModel?.let {
            val exercisesDescriptionModel = it.exercisesResponseData.exercisesDescription
            OneShotPreDrawListener.add(exercisesDescription1ParentLinearLayout){
                formView(exercisesDescriptionModel)
            }
            if(!this.isHidden){
            exercisesDescription1LayoutScrollView.viewTreeObserver.addOnScrollChangedListener(this)
            OneShotPreDrawListener.add(exercisesDescription1LayoutScrollView) {
                if (exercisesDescription1LayoutScrollView.height < exercisesDescription1LayoutScrollView.getChildAt(
                        0
                    ).height
                    + exercisesDescription1LayoutScrollView.paddingTop + exercisesDescription1LayoutScrollView.paddingBottom
                ) {
                    exercisesViewModel?.showButtonDivider(true)
                }
            }
            }
        }
    }

    private fun formView(exercisesDescriptionModel: ExercisesDescriptionData){
        var childIndex = -1
        var insideBrackets = "0"
        for(child in exercisesDescription1ParentLinearLayout.children){
            if(child is TextView){
                if(childIndex >= 0) {
                    val text = exercisesDescriptionModel.textParts[childIndex]
                    val m =
                        Pattern.compile("\\{.*?\\}").matcher(text)
                    var spanIndex = 1
                    val builder = SpannableStringBuilder()
                    while (m.find()) {
                        insideBrackets =
                            exercisesDescriptionModel.partsToInject[m.group(0).replace(
                                """[{}]""".toRegex(),
                                ""
                            ).toInt()]
                        val textSpanParts: ArrayList<SpannableString> = ArrayList(text.split(
                            "\\{.*?\\}".toRegex()).map{span -> SpannableString(span) })
                        textSpanParts.add(spanIndex, insideBrackets.createSpannableString(color = ContextCompat.getColor(child.context, R.color.green), typeFace = ResourcesCompat.getFont(child.context, R.font.roboto_medium)))
                        textSpanParts.forEach{part-> builder.append(part)}
                        spanIndex++
                    }
                    if(spanIndex == 1)
                        child.text = text
                    else
                        child.text = builder
                }
                childIndex++
            }else{
                if(child is LinearLayout){
                    fillLinearLayout(child, insideBrackets.toInt())
                }
            }
        }
    }

    private fun fillLinearLayout(child: LinearLayout, imgCount: Int){
        for(i in 0 until imgCount) {
            val shapeView = View(child.context)
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.height = child.height
            params.width = child.height
            if(i != imgCount-1)
                params.marginEnd = 16.dpToPx()
            shapeView.layoutParams = params
            shapeView.background =  ContextCompat.getDrawable(child.context, R.drawable.exercise_rectangle)
            child.addView(shapeView)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            checkScrollY()
        }else {
            exercisesViewModel?.showToolbarDivider(false)
            exercisesViewModel?.showButtonDivider(false)
            exercisesDescription1LayoutScrollView.viewTreeObserver.removeOnScrollChangedListener(this)
        }
    }

    override fun onScrollChanged() {
        checkScrollY()
    }

    private fun checkScrollY(){
        val scrollY: Int = exercisesDescription1LayoutScrollView.scrollY
        exercisesViewModel?.showToolbarDivider(scrollY != 0)
    }

}