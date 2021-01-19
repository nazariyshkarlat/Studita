package com.studita.presentation.adapter.achievements

import android.content.Context
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.buildSpannedString
import coil.util.CoilUtils
import com.studita.R
import com.studita.domain.entity.AchievementDataData
import com.studita.domain.entity.AchievementLevel
import com.studita.utils.ThemeUtils
import com.studita.utils.createSpannableString
import com.studita.utils.loadSVG
import com.studita.utils.loadSVGIntoMultipleTargets
import kotlinx.android.synthetic.main.improvable_achievement_item.view.*

class ImprovableAchievementViewHolder(view: View) :
    AchievementViewHolder<AchievementDataData>(view) {

    override fun bind(model: AchievementDataData) {
        model as AchievementDataData.ImprovableAchievementData
        fillImages(itemView, model.currentLevelIcon, model.nextLevelIcon, model.currentLevel, model.maxLevel)
        fillText(itemView, model)

        if(!achievementIsCompleted(model.currentLevel, model.maxLevel)) {
            itemView.improvableAchievementItemAchievementProgress.currentProgress =
                model.currentProgress.toFloat() / model.maxProgress
            itemView.improvableAchievementItemProgressBlock.visibility = View.VISIBLE
        }else{
            itemView.improvableAchievementItemProgressBlock.visibility = View.GONE
        }
    }

    private fun fillImages(itemView: View, currentLevelIcon: String?, nextLevelIcon: String?, currentLevel: AchievementLevel, maxLevel: AchievementLevel) {
        with(itemView) {
            loadSVGIntoMultipleTargets(
                imageViews = if(!achievementIsCompleted(currentLevel, maxLevel)) listOf(improvableAchievementItemNextLevelIcon, improvableAchievementItemNextLevelSmallIcon) else listOf(improvableAchievementItemNextLevelIcon),
                url = if (!achievementIsCompleted(currentLevel, maxLevel)) nextLevelIcon!! else currentLevelIcon!!,
                placeholder = R.drawable.achievement_placeholder)


            if (currentLevel != AchievementLevel.NO_LEVEL && currentLevel != maxLevel) {
                improvableAchievementItemCurrentLevelIcon.visibility = View.VISIBLE
                improvableAchievementItemCurrentLevelIcon.loadSVG(
                    currentLevelIcon!!,
                    R.drawable.achievement_placeholder
                )
            } else {
                improvableAchievementItemCurrentLevelIcon.visibility = View.GONE
                CoilUtils.clear(improvableAchievementItemCurrentLevelIcon)
            }
        }
    }

    private fun fillText(itemView: View, model: AchievementDataData.ImprovableAchievementData) {
        with(itemView) {
            improvableAchievementItemTitle.text = model.title
            improvableAchievementItemExercise.text = model.exercise
            if (!achievementIsCompleted(model.currentLevel, model.maxLevel)) {
                improvableAchievementItemReward.text = model.reward
                improvableAchievementItemAchievementProgressText.text = resources.getString(
                    R.string.double_dots_placeholder,
                    model.progressType, resources.getString(R.string.of_template, model.currentProgress, model.maxProgress))
            }else{
                improvableAchievementItemReward.visibility = View.GONE
                improvableAchievementItemAchievementProgressText.visibility = View.GONE
            }
            improvableAchievementItemAchievementLevel.text = createAchievementLevelText(context, model.currentLevel, model.maxLevel)
        }
    }

    private fun createAchievementLevelText(context: Context, currentLevel: AchievementLevel, maxLevel: AchievementLevel) =
        buildSpannedString {
            append(
                context.resources.getString(R.string.double_dots_placeholder, context.resources.getString(R.string.achievement_level), "").createSpannableString(
                    color = ThemeUtils.getPrimaryColor(context),
                    fontSize = 14F,
                    typeFace = ResourcesCompat.getFont(
                        context,
                        R.font.roboto_medium
                    )
                )
            )
            append(
                context.resources.getString(R.string.of_template, currentLevel.levelNumber, maxLevel.levelNumber)
                    .createSpannableString(
                        color = if(!achievementIsCompleted(currentLevel, maxLevel)) ThemeUtils.getSecondaryColor(context) else ThemeUtils.getAccentColor(context),
                        fontSize = 14F,
                        typeFace = ResourcesCompat.getFont(
                            context,
                            R.font.roboto_regular
                        )
                    )
            )
        }


    private fun achievementIsCompleted(currentLevel: AchievementLevel, maxLevel: AchievementLevel) = currentLevel == maxLevel

}