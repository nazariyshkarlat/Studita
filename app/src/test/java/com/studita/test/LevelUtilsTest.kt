package com.studita.test

import com.studita.domain.entity.UserDataData
import com.studita.presentation.model.ExerciseResultAnimation
import com.studita.utils.LevelUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class LevelUtilsTest {

    @Test
    fun getObtainedXP_isTraining_noBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(300), 0.5F, true, 5)
        assertEquals(50 + 25, obtainedXP)
    }

    @Test
    fun getObtainedXP_isTraining_levelBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(450), 0.5F, true, 5)
        assertEquals(100 + 25, obtainedXP)
    }

    @Test
    fun getObtainedXP_isTraining_allCorrectBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(400), 1F, true, 5)
        assertEquals(50 + 25, obtainedXP)
    }

    @Test
    fun getObtainedXP_isTraining_levelBonus_allCorrectBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(450), 1F, true, 5)
        assertEquals(100 + 25, obtainedXP)
    }

    @Test
    fun getObtainedXP_isNotTraining_noBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(300), 0.3F, false, 6)
        assertEquals(30 + 60, obtainedXP)
    }

    @Test
    fun getObtainedXP_isNotTraining_levelBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(450), 0.5F, false, 6)
        assertEquals(100 + 60, obtainedXP)
    }

    @Test
    fun getObtainedXP_isNotTraining_allCorrectBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(300), 1F, false, 6)
        assertEquals(200 + 60, obtainedXP)
    }

    @Test
    fun getObtainedXP_isNotTraining_levelBonus_allCorrectBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(450), 1F, false, 6)
        assertEquals(200 + 60, obtainedXP)
    }


    @Test
    fun getObtainedBonus_isTraining_noBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(300), 0.5F, true, 0)
        assertEquals(0, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isTraining_levelUpBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(450), 0.5F, true, 2)
        assertEquals(50 + 10, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isTraining_allCorrectBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(300), 1F, true, 2)
        assertEquals(0 + 10, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isTraining_levelUp_allCorrectBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(450), 1F, true, 6)
        assertEquals(80, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isNotTraining_noBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(300), 0.5F, false, 0)
        assertEquals(0, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isNotTraining_levelUpBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(450), 0.5F, false, 0)
        assertEquals(50, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isNotTraining_allCorrectBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(300), 1F, false, 0)
        assertEquals(50, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isNotTraining_levelUp_allCorrectBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(450), 1F, false, 0)
        assertEquals(100, obtainedBonus)
    }


    @Test
    fun getNewLevelsCount_noNew() {
        val newLevels = LevelUtils.getNewLevelsCount(getUserDataWithXP(0), 100)
        assertEquals(0, newLevels)
    }

    @Test
    fun getNewLevelsCount_newLevels() {
        val newLevels = LevelUtils.getNewLevelsCount(getUserDataWithXP(0), 9500)
        assertEquals(10, newLevels)
    }

    @Test
    fun getNewLevelXP_equalsOld() {
        val newLevels = LevelUtils.getNewLevelXP(getUserDataWithXP(0), 400)
        assertEquals(400, newLevels)
    }

    @Test
    fun getNewLevelXP_newIsEmpty() {
        val newLevels = LevelUtils.getNewLevelXP(getUserDataWithXP(100), 400)
        assertEquals(0, newLevels)
    }

    @Test
    fun getNewLevelXP_newXP() {
        val newLevels = LevelUtils.getNewLevelXP(getUserDataWithXP(100), 9600)
        assertEquals(200, newLevels)
    }

    @Test
    fun getExerciseResultAnimation() {
        assertEquals(
            LevelUtils.getExerciseResultAnimation(getUserDataWithXP(665), 1F, true, 9),
            arrayListOf(
                ExerciseResultAnimation.ObtainedXP(1F),
                ExerciseResultAnimation.ObtainedXP(15 / 800F),
                ExerciseResultAnimation.BonusExercisesBonus(60 / 800F),
                ExerciseResultAnimation.LevelUPBonus(110 / 800F)
            )
        )
    }

    private fun getUserDataWithXP(currentLevelXP: Int) = UserDataData(
        0,
        "",
        "",
        "",
        null,
        3,
        currentLevelXP,
        0,
        false,
        arrayListOf(2, 0, 0, 0),
        Date(),
        1,
        true
    )

}