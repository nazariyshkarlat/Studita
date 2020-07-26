package com.example.studita.domain.interactor

import com.example.studita.domain.entity.*
import com.example.studita.domain.entity.authorization.LogInResponseData
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.domain.entity.exercise.ExercisesResponseData
import com.google.gson.annotations.SerializedName
import java.util.ArrayList


sealed class LevelsStatus {
    object ServiceUnavailable : LevelsStatus()
    object NoConnection : LevelsStatus()
    object Failure: LevelsStatus()
    data class Success(val result: List<LevelData>) : LevelsStatus()
}


sealed class LevelsCacheStatus {
    object ServiceUnavailable : LevelsCacheStatus()
    object NoConnection : LevelsCacheStatus()
    object IsCached : LevelsCacheStatus()
    object Success : LevelsCacheStatus()
    object Failure : LevelsCacheStatus()
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
    object Failure: ChapterStatus()
    data class Success(val result: ChapterData): ChapterStatus()
}

sealed class ChaptersCacheStatus{
    object ServiceUnavailable : ChaptersCacheStatus()
    object NoConnection : ChaptersCacheStatus()
    object IsCached : ChaptersCacheStatus()
    object Failure: ChaptersCacheStatus()
    object Success: ChaptersCacheStatus()
}

sealed class ExercisesStatus{
    object ServiceUnavailable : ExercisesStatus()
    object Failure : ExercisesStatus()
    object NoConnection : ExercisesStatus()
    object NoChapterPartFound: ExercisesStatus()
    data class Success(val result: ExercisesResponseData): ExercisesStatus()
}

sealed class ExerciseResultStatus{
    object ServiceUnavailable : ExerciseResultStatus()
    object NoConnection : ExerciseResultStatus()
    object NoExerciseFound: ExerciseResultStatus()
    object Failure: ExerciseResultStatus()
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
    object Failure : InterestingStatus()

    data class Success(val result: InterestingData): InterestingStatus()
}

sealed class InterestingCacheStatus{
    object ServiceUnavailable : InterestingCacheStatus()
    object NoConnection : InterestingCacheStatus()
    object IsCached : InterestingCacheStatus()
    object Success: InterestingCacheStatus()
    object Failure : InterestingCacheStatus()
}

sealed class SubscribeEmailResultStatus{
    object ServiceUnavailable : SubscribeEmailResultStatus()
    object NoConnection : SubscribeEmailResultStatus()
    object Failure: SubscribeEmailResultStatus()
    data class Success(val result: SubscribeEmailResultData): SubscribeEmailResultStatus()
}

sealed class SaveObtainedExercisesDataStatus{
    object ServiceUnavailable : SaveObtainedExercisesDataStatus()
    object NoConnection : SaveObtainedExercisesDataStatus()
    object Failure: SaveObtainedExercisesDataStatus()
    object Success: SaveObtainedExercisesDataStatus()
}

sealed class ExercisesCacheStatus{
    object ServiceUnavailable : ExercisesCacheStatus()
    object NoConnection : ExercisesCacheStatus()
    object IsCached : ExercisesCacheStatus()
    object Success: ExercisesCacheStatus()
    object Failure : ExercisesCacheStatus()
}

sealed class CompleteExercisesStatus{
    object ServiceUnavailable : CompleteExercisesStatus()
    object NoConnection : CompleteExercisesStatus()
    object Failure : CompleteExercisesStatus()
    object Success: CompleteExercisesStatus()
}

sealed class EditProfileStatus{
    object ServiceUnavailable : EditProfileStatus()
    object NoConnection : EditProfileStatus()
    object Failure: EditProfileStatus()
    class Success(val avatarLink: String?): EditProfileStatus()
}

sealed class UserNameAvailableStatus{
    object Failure: UserNameAvailableStatus()
    object Available: UserNameAvailableStatus()
    object Unavailable: UserNameAvailableStatus()
}

sealed class PrivacySettingsStatus{
    object ServiceUnavailable : PrivacySettingsStatus()
    object NoConnection : PrivacySettingsStatus()
    object Failure: PrivacySettingsStatus()
    class Success(val privacySettingsData: PrivacySettingsData): PrivacySettingsStatus()
}

sealed class EditPrivacySettingsStatus{
    object ServiceUnavailable : EditPrivacySettingsStatus()
    object NoConnection : EditPrivacySettingsStatus()
    object Failure: EditPrivacySettingsStatus()
    object Success: EditPrivacySettingsStatus()
}

sealed class GetUsersStatus{
    object ServiceUnavailable : GetUsersStatus()
    object NoConnection : GetUsersStatus()
    object Failure: GetUsersStatus()
    object NoUsersFound: GetUsersStatus()
    class Success(val friendsResponseData: UsersResponseData): GetUsersStatus()
}

sealed class IsMyFriendStatus{

    sealed class Success(@Transient open val userId: Int) : IsMyFriendStatus(){
        class IsMyFriend(override val userId: Int) : Success(userId)
        class IsNotMyFriend(override val userId: Int) : Success(userId)
        class GotMyFriendshipRequest(override val userId: Int) : Success(userId)
        class WaitingForFriendshipAccept(override val userId: Int) : Success(userId)
    }

    object ServiceUnavailable : IsMyFriendStatus()
    object NoConnection : IsMyFriendStatus()
    object Failure: IsMyFriendStatus()
}

sealed class FriendActionStatus{
    object ServiceUnavailable : FriendActionStatus()
    object NoConnection : FriendActionStatus()
    object Failure: FriendActionStatus()
    object Success: FriendActionStatus()
}

sealed class PrivacySettingsDuelsExceptionsStatus{
    object ServiceUnavailable : PrivacySettingsDuelsExceptionsStatus()
    object NoConnection : PrivacySettingsDuelsExceptionsStatus()
    object NoUsersFound: PrivacySettingsDuelsExceptionsStatus()
    object Failure: PrivacySettingsDuelsExceptionsStatus()
    class Success(val privacySettingsDuelsExceptionsItems: List<PrivacyDuelsExceptionData>): PrivacySettingsDuelsExceptionsStatus()
}

sealed class EditDuelsExceptionsStatus{
    object ServiceUnavailable : EditDuelsExceptionsStatus()
    object NoConnection : EditDuelsExceptionsStatus()
    object Failure: EditDuelsExceptionsStatus()
    object Success: EditDuelsExceptionsStatus()
}

sealed class HasFriendsStatus{
    object ServiceUnavailable : HasFriendsStatus()
    object NoConnection : HasFriendsStatus()
    object Failure: HasFriendsStatus()
    object HasFriends: HasFriendsStatus()
    object HasNoFriends: HasFriendsStatus()
}

sealed class GetNotificationsStatus{
    object ServiceUnavailable : GetNotificationsStatus()
    object NoConnection : GetNotificationsStatus()
    object Failure: GetNotificationsStatus()
    object NoNotificationsFound: GetNotificationsStatus()
    class Success(val notificationsData: ArrayList<NotificationData>): GetNotificationsStatus()
}

sealed class CheckTokenIsCorrectStatus{
    object ServiceUnavailable : CheckTokenIsCorrectStatus()
    object NoConnection : CheckTokenIsCorrectStatus()
    object Failure: CheckTokenIsCorrectStatus()
    object Correct: CheckTokenIsCorrectStatus()
    object Waiting: CheckTokenIsCorrectStatus()
    object Incorrect: CheckTokenIsCorrectStatus()
}

sealed class SetNotificationsAreCheckedStatus{
    object ServiceUnavailable : SetNotificationsAreCheckedStatus()
    object NoConnection : SetNotificationsAreCheckedStatus()
    object Failure: SetNotificationsAreCheckedStatus()
    object Success: SetNotificationsAreCheckedStatus()
}


