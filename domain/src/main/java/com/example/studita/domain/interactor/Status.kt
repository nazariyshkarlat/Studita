package com.example.studita.domain.interactor

import com.example.studita.domain.entity.*
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

sealed class SignInWithGoogleStatus{
    object ServiceUnavailable : SignInWithGoogleStatus()
    object NoConnection : SignInWithGoogleStatus()
    object Failure: SignInWithGoogleStatus()
    data class Success(val result: LogInResponseData): SignInWithGoogleStatus()
}

sealed class ChapterStatus{
    object ServiceUnavailable : ChapterStatus()
    object NoConnection : ChapterStatus()
    object NoChapterFound: ChapterStatus()
    data class Success(val result: ChapterData): ChapterStatus()
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

sealed class UserDataStatus{
    object ServiceUnavailable : UserDataStatus()
    object NoConnection : UserDataStatus()
    object Failure: UserDataStatus()
    data class Success(val result: UserDataData): UserDataStatus()
}

sealed class UserStatisticsStatus{
    object ServiceUnavailable : UserStatisticsStatus()
    object NoConnection : UserStatisticsStatus()
    object Failure: UserStatisticsStatus()
    data class Success(val results: List<UserStatisticsData>): UserStatisticsStatus()
}

sealed class InterestingStatus{
    object ServiceUnavailable : InterestingStatus()
    object NoConnection : InterestingStatus()
    object NoInterestingFound: InterestingStatus()
    data class Success(val result: InterestingData): InterestingStatus()
}

sealed class SubscribeEmailResultStatus{
    object ServiceUnavailable : SubscribeEmailResultStatus()
    object NoConnection : SubscribeEmailResultStatus()
    object Failure: SubscribeEmailResultStatus()
    data class Success(val result: SubscribeEmailResultData): SubscribeEmailResultStatus()
}

sealed class SaveObtainedExerciseDataStatus{
    object ServiceUnavailable : SaveObtainedExerciseDataStatus()
    object NoConnection : SaveObtainedExerciseDataStatus()
    object Failure: SaveObtainedExerciseDataStatus()
    object Success: SaveObtainedExerciseDataStatus()
}