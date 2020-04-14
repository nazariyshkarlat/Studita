package com.example.studita.presentation.utils

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import com.example.studita.R
import com.example.studita.domain.entity.UserDataData

object LevelUtils {

    const val NEXT_LEVEL_BONUS = 50

    const val TRAINING_XP = 50

    private const val FIRST_LEVEL_XP = 500

    fun getProgressText(completedParts: Int, chapterPartsCount: Int, context: Context): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        if((completedParts != chapterPartsCount) or  (chapterPartsCount == 0)) {
            val text = context.resources.getString(
                R.string.chapter_progress,
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

    fun getLevelProgressPercent(userData: UserDataData, obtainedXP: Int = 0) = obtainedXP/getLevelXP(userData.currentLevel).toFloat()

    fun getXPFromPercent(percent: Float) = (percent*100).toInt()

    fun getObtainedXPWithBonuses(userData: UserDataData, exerciseObtainedXP: Int) = exerciseObtainedXP + (if(isNewLevel(userData, exerciseObtainedXP)) NEXT_LEVEL_BONUS else 0)

    fun isNewLevel(userData: UserDataData, obtainedXP: Int) = ((userData.currentLevelXP + obtainedXP) - getLevelXP(userData.currentLevel)) >= 0

    fun getNewLevelXP(userData: UserDataData, obtainedXP: Int): Int {
        val newXPDirty = userData.currentLevelXP + obtainedXP
        val XPDifference = newXPDirty - getLevelXP(userData.currentLevel)
        return if (XPDifference >= 0){
            XPDifference
        }else{
            newXPDirty
        }
    }
}