package com.example.studita.utils

import android.content.Context
import android.text.SpannableStringBuilder
import com.example.studita.R
import com.example.studita.domain.entity.UserDataData
import com.example.studita.presentation.model.ExerciseResultAnimation
import java.io.IOException
import kotlin.collections.ArrayList

object LevelUtils {

    private const val SEQUENCE_TO_BONUS = 5
    const val SEQUENCE_BONUS = 100
    const val NEXT_LEVEL_BONUS = 50
    const val ALL_CORRECT_BONUS = 50
    private const val TRAINING_XP = 50
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
                    color = ColorUtils.getAccentColor(context)
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
        return getObtainedBonus(userData, percent, isTraining) + percentToXP(percent, isTraining)
    }

    fun getObtainedBonus(userData: UserDataData, percent: Float, isTraining: Boolean) : Int{
        val withoutLevelBonus: Int = (if(!isTraining && (percent == 1F)) ALL_CORRECT_BONUS else 0) + (if(giveSequenceBonus(userData, isTraining)) SEQUENCE_BONUS else 0)
        val obtainedXP = withoutLevelBonus + percentToXP(percent, isTraining)
        return getNewLevelsBonus(userData, obtainedXP)+ withoutLevelBonus
    }

    private fun getNewLevelsBonus(userData: UserDataData, obtainedXP: Int): Int {
        return getNewLevelsCount(
            userData,
            obtainedXP
        ) * NEXT_LEVEL_BONUS
    }

    fun getNewLevelsCount(userData: UserDataData, obtainedXP: Int) : Int {
        var newLevels = 0
        var levelXP = obtainedXP + userData.currentLevelXP

        var maxLevelXP = getLevelXP(userData.currentLevel + newLevels)

        while (levelXP >= maxLevelXP) {
            levelXP -= getLevelXP(userData.currentLevel + newLevels)
            newLevels++
            maxLevelXP = getLevelXP(userData.currentLevel + newLevels)
        }
        return newLevels
    }

    fun getNewLevelXP(userData: UserDataData, obtainedXP: Int): Int {
        var newLevels = 0
        val newXPDirty = userData.currentLevelXP + obtainedXP
        var newXP = newXPDirty

        var maxLevelXP = getLevelXP(userData.currentLevel + newLevels)

        while (newXP >= maxLevelXP) {
            newXP -= maxLevelXP
            newLevels++
            maxLevelXP = getLevelXP(userData.currentLevel + newLevels)
        }
        return newXP
    }

    private fun giveSequenceBonus(userData: UserDataData, isTraining: Boolean) = (userData.todayCompletedExercises % SEQUENCE_TO_BONUS  == 0) and (!isTraining)

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

        val newLevelsAfterAllCorrectBonus = getNewLevelsCount(oldUserData, bonus)
        oldUserData.currentLevelXP = getNewLevelXP(oldUserData, bonus)

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

        bonus = if(giveSequenceBonus(oldUserData, isTraining)) SEQUENCE_BONUS else 0

        val newLevelsAfterSequenceBonus = getNewLevelsCount(oldUserData, bonus)
        oldUserData.currentLevelXP = getNewLevelXP(oldUserData, bonus)

        if (bonus != 0) {
            repeat(newLevelsAfterSequenceBonus) {
                exerciseResultAnimation.add(
                    ExerciseResultAnimation.SequenceBonus(
                        1F
                    )
                )
                oldUserData.currentLevel++
            }

            if (oldUserData.currentLevelXP != 0) {
                exerciseResultAnimation.add(
                    ExerciseResultAnimation.SequenceBonus(
                        getLevelProgressPercent(
                            oldUserData
                        )
                    )
                )
            }
        }

        bonus = (newLevelsAfterAllCorrectBonus + newLevelsBeforeBonus + newLevelsAfterSequenceBonus) * NEXT_LEVEL_BONUS

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