package com.example.studita.presentation.utils

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import com.example.studita.R
import com.example.studita.domain.entity.UserDataData
import com.example.studita.presentation.model.ExerciseResultAnimation
import java.io.IOException
import kotlin.collections.ArrayList

object LevelUtils {

    const val NEXT_LEVEL_BONUS = 50

    const val ALL_CORRECT_BONUS = 50

    const val TRAINING_XP = 50

    private const val FIRST_LEVEL_XP = 500

    fun getProgressText(completedParts: Int, chapterPartsCount: Int, context: Context): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        if((completedParts != chapterPartsCount) or  (chapterPartsCount == 0)) {
            val text = LanguageUtils.getResourcesRussianLocale(context)?.getQuantityString(
                R.plurals.chapter_progress_plurals,
                completedParts,
                completedParts,
                chapterPartsCount
            )
            builder.append(text)
        }else {
            val text = context.resources.getString(
                R.string.chapter_full_progress
            )
            builder.append(text.substring(0, text.indexOf(" ")))
            builder.append(
                text.substring(
                    text.indexOf(" ")
                ).createSpannableString(
                    color = ContextCompat.getColor(context, R.color.blue)
                )
            )
        }
        return builder
    }

    fun getLevelXP(currentLevelNumber: Int) = FIRST_LEVEL_XP + (currentLevelNumber-1)*100

    fun getNextLevel(currentLevelNumber: Int) = currentLevelNumber+1

    fun getChapterProgressPercent(completedParts: Int, chapterPartsCount: Int) = completedParts/chapterPartsCount.toFloat()

    fun getLevelProgressPercent(userData: UserDataData) = userData.currentLevelXP/getLevelXP(userData.currentLevel).toFloat()

    fun percentToXP(percent: Float, isTraining: Boolean) = if(percent > 1F) throw IOException("percent more than 100!") else if(isTraining) TRAINING_XP else (percent*100).toInt()

    fun getObtainedXP(userData: UserDataData, percent: Float, isTraining: Boolean) : Int{
        val withoutLevelBonus: Int = if(isTraining){
            TRAINING_XP
        }else {
            (percent * 100).toInt() + (if(percent == 1F) ALL_CORRECT_BONUS else 0)
        }
        return getNewLevelsCount(userData, withoutLevelBonus)* NEXT_LEVEL_BONUS + withoutLevelBonus
    }

    fun getObtainedBonus(userData: UserDataData, percent: Float, isTraining: Boolean) : Int{
        val withoutLevelBonus: Int = if(!isTraining) (if(percent == 1F) ALL_CORRECT_BONUS else 0) else 0
        return getNewLevelsCount(userData, withoutLevelBonus+ percentToXP(percent, isTraining))* NEXT_LEVEL_BONUS + withoutLevelBonus
    }

    fun getNewLevelsCount(userData: UserDataData, obtainedXP: Int) : Int {
        var newLevels = 0
        var levelXP = obtainedXP + userData.currentLevelXP

        while (levelXP >= getLevelXP(userData.currentLevel + newLevels)) {
            levelXP -= getLevelXP(userData.currentLevel + newLevels)
            newLevels++
        }
        return newLevels
    }

    fun getNewLevelXP(userData: UserDataData, obtainedXP: Int): Int {
        var newLevels = 0
        val newXPDirty = userData.currentLevelXP + obtainedXP
        var newXP = newXPDirty

        while (newXP >= getLevelXP(userData.currentLevel + newLevels)) {
            newXP -= getLevelXP(userData.currentLevel + newLevels)
            newLevels++
        }
        return newXP
    }

    fun getExerciseResultAnimation(userData: UserDataData, percent: Float, isTraining: Boolean): List<ExerciseResultAnimation> {

        val oldUserData = userData.copy()

        val exerciseResultAnimation = ArrayList<ExerciseResultAnimation>()

        val obtainedXP = percentToXP(percent, isTraining)
        val newLevelsBeforeBonus = getNewLevelsCount(oldUserData, obtainedXP)

        oldUserData.currentLevelXP = getNewLevelXP(oldUserData, obtainedXP)

        repeat(newLevelsBeforeBonus) {
            exerciseResultAnimation.add(
                ExerciseResultAnimation.ObtainedXP(
                    1F
                )
            )
            oldUserData.currentLevel++
        }

        if (oldUserData.currentLevelXP != 0) {
            exerciseResultAnimation.add(
                ExerciseResultAnimation.ObtainedXP(
                    getLevelProgressPercent(
                        oldUserData
                    )
                )
            )
        }

        var bonus = if (!isTraining and (percent == 1F)) ALL_CORRECT_BONUS else 0

        oldUserData.currentLevelXP = getNewLevelXP(oldUserData, bonus)

        val newLevelsAfterAllCorrectBonus = getNewLevelsCount(oldUserData, bonus)

        if (bonus != 0) {
            repeat(newLevelsAfterAllCorrectBonus) {
                exerciseResultAnimation.add(
                    ExerciseResultAnimation.AllCorrectBonus(
                        1F
                    )
                )
                oldUserData.currentLevel++
            }

            if (oldUserData.currentLevelXP != 0) {
                exerciseResultAnimation.add(
                    ExerciseResultAnimation.AllCorrectBonus(
                        getLevelProgressPercent(
                            oldUserData
                        )
                    )
                )
            }
        }

        bonus = (newLevelsAfterAllCorrectBonus + newLevelsBeforeBonus) * NEXT_LEVEL_BONUS

        oldUserData.currentLevelXP +=  bonus

        if (bonus != 0) {

            while (oldUserData.currentLevelXP >= getLevelXP(oldUserData.currentLevel)) {
                oldUserData.currentLevelXP += NEXT_LEVEL_BONUS
                oldUserData.currentLevelXP -= getLevelXP(oldUserData.currentLevel)
                oldUserData.currentLevel++
                exerciseResultAnimation.add(ExerciseResultAnimation.LevelUPBonus(1F))
            }


            if (oldUserData.currentLevelXP != 0) {
                exerciseResultAnimation.add(
                    ExerciseResultAnimation.LevelUPBonus(
                        getLevelProgressPercent(
                            oldUserData
                        )
                    )
                )
            }
        }

        println(exerciseResultAnimation)

        return exerciseResultAnimation
    }
}