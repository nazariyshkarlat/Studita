package com.studita.presentation.adapter.achievements

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.buildSpannedString
import androidx.core.view.updateLayoutParams
import com.studita.R
import com.studita.domain.entity.AchievementDataData
import com.studita.utils.*
import kotlinx.android.synthetic.main.non_improvable_achievement_item.view.*

class NonImprovableAchievementViewHolder(view: View) :
    AchievementViewHolder<AchievementDataData>(view) {

    override fun bind(model: AchievementDataData) {
        model as AchievementDataData.NonImprovableAchievementData
        itemView.nonImprovableAchievementItemNextLevelIcon.loadSVG(
            model.iconUrl,
            R.drawable.achievement_placeholder
        )
        fillText(itemView, model)
        formMargin()
    }

    private fun formMargin(){
        if(absoluteAdapterPosition == 0){
            itemView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = 0
            }
        }else{
            itemView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = 8F.dp
            }
        }
    }

    private fun fillText(itemView: View, model: AchievementDataData.NonImprovableAchievementData) {
        with(itemView) {
            nonImprovableAchievementItemTitle.text = model.title
            nonImprovableAchievementItemExercise.text = model.exercise
            nonImprovableAchievementItemReward.text =model. reward
            nonImprovableAchievementItemAchievementLevel.text = createIsCompletedText(context, model.isCompleted)
        }
    }

    private fun createIsCompletedText(context: Context, isCompleted: Boolean) =
        buildSpannedString {
            append(
                context.resources.getString(R.string.double_dots_placeholder, context.resources.getString(R.string.progress), "").createSpannableString(
                    color = ThemeUtils.getPrimaryColor(context),
                    fontSize = 14F,
                    typeFace = ResourcesCompat.getFont(
                        context,
                        R.font.roboto_medium
                    )
                )
            )
            append(
                context.resources.getString(
                    when {
                        !UserUtils.isLoggedIn() -> R.string.unavailable
                        !isCompleted -> R.string.is_not_completed
                        else -> R.string.is_completed
                    }
                )
                    .createSpannableString(
                        color = if(!isCompleted) ThemeUtils.getSecondaryColor(context) else ThemeUtils.getAccentColor(context),
                        fontSize = 14F,
                        typeFace = ResourcesCompat.getFont(
                            context,
                            R.font.roboto_regular
                        )
                    )
            )
        }

}