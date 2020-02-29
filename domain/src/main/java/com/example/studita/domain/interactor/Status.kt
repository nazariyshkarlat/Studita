package com.example.studita.domain.interactor

import com.example.studita.domain.entity.ChapterPartData
import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.LevelData
import com.example.studita.domain.entity.authorization.LogInResponseData
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.domain.entity.exercise.ExercisesResponseData


sealed class LevelsStatus {
    object ServiceUnavailable : LevelsStatus()
    object NoConnection : LevelsStatus()
    data class Success(val result: List<LevelData>) : LevelsStatus()
}

sealed class SignUpStatus{
    object ServiceUnavailable : SignUpStatus()
    object NoConnection : SignUpStatus()
    object Failure: SignUpStatus()
    object UserAlreadyExists : SignUpStatus()
    object Success: SignUpStatus()
}

sealed class LogInStatus{
    object ServiceUnavailable : LogInStatus()
    object NoConnection : LogInStatus()
    object Failure: LogInStatus()
    object NoUserFound : LogInStatus()
    data class Success(val result: LogInResponseData): LogInStatus()
}

sealed class ChapterPartsStatus{
    object ServiceUnavailable : ChapterPartsStatus()
    object NoConnection : ChapterPartsStatus()
    object NoChapterFound: ChapterPartsStatus()
    data class Success(val result: List<ChapterPartData>): ChapterPartsStatus()
}

sealed class ExercisesStatus{
    object ServiceUnavailable : ExercisesStatus()
    object NoConnection : ExercisesStatus()
    object NoChapterPartFound: ExercisesStatus()
    data class Success(val result: ExercisesResponseData): ExercisesStatus()
}

sealed class ExerciseResultStatus{
    object ServiceUnavailable : ExerciseResultStatus()
    object NoConnection : ExerciseResultStatus()
    object NoExerciseFound: ExerciseResultStatus()
    data class Success(val result: ExerciseResponseData): ExerciseResultStatus()
}