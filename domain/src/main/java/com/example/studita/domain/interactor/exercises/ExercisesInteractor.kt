package com.example.studita.domain.interactor.exercises

import com.example.studita.domain.interactor.ChapterPartsStatus
import com.example.studita.domain.interactor.ExercisesStatus

interface ExercisesInteractor {

    suspend fun getExercises(chapterPartNumber: Int) : ExercisesStatus

}