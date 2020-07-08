package com.example.studita.test

import com.example.studita.domain.entity.UserDataData
import com.example.studita.presentation.model.ExerciseResultAnimation
import com.example.studita.utils.LevelUtils
import org.junit.Test

import org.junit.Assert.*
import java.util.*

class LevelUtilsTest {

    @Test
    fun getObtainedXP_isTraining_noBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(300), 0.5F, true)
        assertEquals(50, obtainedXP)
    }

    @Test
    fun getObtainedXP_isTraining_levelBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(450), 0.5F, true)
        assertEquals(100, obtainedXP)
    }

    @Test
    fun getObtainedXP_isTraining_allCorrectBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(400), 1F, true)
        assertEquals(50, obtainedXP)
    }

    @Test
    fun getObtainedXP_isTraining_levelBonus_allCorrectBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(450), 1F, true)
        assertEquals(100, obtainedXP)
    }

    @Test
    fun getObtainedXP_isNotTraining_noBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(300), 0.3F, false)
        assertEquals(30, obtainedXP)
    }

    @Test
    fun getObtainedXP_isNotTraining_levelBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(450), 0.5F, false)
        assertEquals(100, obtainedXP)
    }

    @Test
    fun getObtainedXP_isNotTraining_allCorrectBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(300), 1F, false)
        assertEquals(150, obtainedXP)
    }

    @Test
    fun getObtainedXP_isNotTraining_levelBonus_allCorrectBonus() {
        val obtainedXP = LevelUtils.getObtainedXP(getUserDataWithXP(450), 1F, false)
        assertEquals(200, obtainedXP)
    }


    @Test
    fun getObtainedBonus_isTraining_noBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(300), 0.5F, true)
        assertEquals(0, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isTraining_levelUpBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(450), 0.5F, true)
        assertEquals(50, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isTraining_allCorrectBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(300), 1F, true)
        assertEquals(0, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isTraining_levelUp_allCorrectBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(450), 1F, true)
        assertEquals(50, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isNotTraining_noBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(300), 0.5F, false)
        assertEquals(0, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isNotTraining_levelUpBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(450), 0.5F, false)
        assertEquals(50, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isNotTraining_allCorrectBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(300), 1F, false)
        assertEquals(50, obtainedBonus)
    }

    @Test
    fun getObtainedBonus_isNotTraining_levelUp_allCorrectBonus() {
        val obtainedBonus = LevelUtils.getObtainedBonus(getUserDataWithXP(450), 1F, false)
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
        assertEquals(LevelUtils.getExerciseResultAnimation(getUserDataWithXP(400), 1F, false), arrayListOf(ExerciseResultAnimation.ObtainedXP(1F), ExerciseResultAnimation.AllCorrectBonus(50/600F), ExerciseResultAnimation.LevelUPBonus(100/600F)))
    }

    private fun getUserDataWithXP(currentLevelXP: Int) = UserDataData(0, "", "", "", null, 1, currentLevelXP, 0, false, arrayListOf(0,0,0,0),  Date(), 1, true)

}