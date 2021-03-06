package com.studita.domain.interactor

import com.studita.domain.entity.*
import com.studita.domain.entity.authorization.LogInResponseData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*


sealed class LevelsStatus {
    object ServiceUnavailable : LevelsStatus()
    object NoConnection : LevelsStatus()
    object Failure : LevelsStatus()
    data class Success(val result: List<LevelData>) : LevelsStatus()
}


sealed class LevelsCacheStatus {
    object ServiceUnavailable : LevelsCacheStatus()
    object NoConnection : LevelsCacheStatus()
    object IsCached : LevelsCacheStatus()
    object Success : LevelsCacheStatus()
    object Failure : LevelsCacheStatus()
}

sealed class SignUpStatus {
    object ServiceUnavailable : SignUpStatus()
    object NoConnection : SignUpStatus()
    object Failure : SignUpStatus()
    object UserAlreadyExists : SignUpStatus()
    object Success : SignUpStatus()
}

sealed class LogInStatus {
    object ServiceUnavailable : LogInStatus()
    object NoConnection : LogInStatus()
    object Failure : LogInStatus()
    object NoUserFound : LogInStatus()
    data class Success(val result: LogInResponseData) : LogInStatus()
}

sealed class SignInWithGoogleStatus {
    object ServiceUnavailable : SignInWithGoogleStatus()
    object NoConnection : SignInWithGoogleStatus()
    object Failure : SignInWithGoogleStatus()
    data class Success(val result: LogInResponseData) : SignInWithGoogleStatus()
}

sealed class ChapterStatus {
    object ServiceUnavailable : ChapterStatus()
    object NoConnection : ChapterStatus()
    object NoChapterFound : ChapterStatus()
    object Failure : ChapterStatus()
    data class Success(val result: ChapterData) : ChapterStatus()
}

sealed class ChaptersCacheStatus {
    object ServiceUnavailable : ChaptersCacheStatus()
    object NoConnection : ChaptersCacheStatus()
    object IsCached : ChaptersCacheStatus()
    object Failure : ChaptersCacheStatus()
    object Success : ChaptersCacheStatus()
}

sealed class UserDataStatus {
    object ServiceUnavailable : UserDataStatus()
    object NoConnection : UserDataStatus()
    object Failure : UserDataStatus()
    data class Success(val result: UserDataData) : UserDataStatus()
}

sealed class UserStatisticsStatus {
    object ServiceUnavailable : UserStatisticsStatus()
    object NoConnection : UserStatisticsStatus()
    object Failure : UserStatisticsStatus()
    data class Success(val results: List<UserStatisticsData>) : UserStatisticsStatus()
}

sealed class InterestingCacheStatus {
    object ServiceUnavailable : InterestingCacheStatus()
    object NoConnection : InterestingCacheStatus()
    object IsCached : InterestingCacheStatus()
    object Success : InterestingCacheStatus()
    object Failure : InterestingCacheStatus()
}

sealed class SubscribeEmailResultStatus {
    object ServiceUnavailable : SubscribeEmailResultStatus()
    object NoConnection : SubscribeEmailResultStatus()
    object Failure : SubscribeEmailResultStatus()
    data class Success(val result: SubscribeEmailResultData) : SubscribeEmailResultStatus()
}

sealed class SaveObtainedExercisesDataStatus {
    object ServiceUnavailable : SaveObtainedExercisesDataStatus()
    object NoConnection : SaveObtainedExercisesDataStatus()
    object Failure : SaveObtainedExercisesDataStatus()
    object Success : SaveObtainedExercisesDataStatus()
}

sealed class ExercisesCacheStatus {
    object ServiceUnavailable : ExercisesCacheStatus()
    object NoConnection : ExercisesCacheStatus()
    object IsCached : ExercisesCacheStatus()
    object Success : ExercisesCacheStatus()
    object Failure : ExercisesCacheStatus()
}

sealed class CompleteExercisesStatus {
    object ServiceUnavailable : CompleteExercisesStatus()
    object NoConnection : CompleteExercisesStatus()
    object Failure : CompleteExercisesStatus()
    object Success : CompleteExercisesStatus()
}

sealed class EditProfileStatus {
    object ServiceUnavailable : EditProfileStatus()
    object NoConnection : EditProfileStatus()
    object Failure : EditProfileStatus()
    class Success(val avatarLink: String?) : EditProfileStatus()
}

sealed class UserNameAvailableStatus {
    object ServiceUnavailable : UserNameAvailableStatus()
    object NoConnection : UserNameAvailableStatus()
    object Failure : UserNameAvailableStatus()
    object Available : UserNameAvailableStatus()
    object Unavailable : UserNameAvailableStatus()
    object IsTaken : UserNameAvailableStatus()
}

sealed class PrivacySettingsStatus {
    object ServiceUnavailable : PrivacySettingsStatus()
    object NoConnection : PrivacySettingsStatus()
    object Failure : PrivacySettingsStatus()
    class Success(val privacySettingsData: PrivacySettingsData) : PrivacySettingsStatus()
}

sealed class EditPrivacySettingsStatus {
    object ServiceUnavailable : EditPrivacySettingsStatus()
    object NoConnection : EditPrivacySettingsStatus()
    object Failure : EditPrivacySettingsStatus()
    object Success : EditPrivacySettingsStatus()
}

sealed class GetUsersStatus {
    object ServiceUnavailable : GetUsersStatus()
    object NoConnection : GetUsersStatus()
    object Failure : GetUsersStatus()
    object NoUsersFound : GetUsersStatus()
    class Success(val friendsResponseData: UsersResponseData) : GetUsersStatus()
}

sealed class IsMyFriendStatus {

    @Serializable
    sealed class Success : IsMyFriendStatus() {
        abstract val userId: Int
        @Serializable
        @SerialName("is_my_friend")
        class IsMyFriend(override val userId: Int) : Success()
        @Serializable
        @SerialName("is_not_my_friend")
        class IsNotMyFriend(override val userId: Int) : Success()
        @Serializable
        @SerialName("got_my_friendship_request")
        class GotMyFriendshipRequest(override val userId: Int) : Success()
        @Serializable
        @SerialName("waiting_for_friendship_accept")
        class WaitingForFriendshipAccept(override val userId: Int) : Success()
    }

    object ServiceUnavailable : IsMyFriendStatus()

    object NoConnection : IsMyFriendStatus()

    object Failure : IsMyFriendStatus()
}

sealed class FriendActionStatus {
    object ServiceUnavailable : FriendActionStatus()
    object NoConnection : FriendActionStatus()
    object Failure : FriendActionStatus()
    object Success : FriendActionStatus()
}

sealed class PrivacySettingsDuelsExceptionsStatus {
    object ServiceUnavailable : PrivacySettingsDuelsExceptionsStatus()
    object NoConnection : PrivacySettingsDuelsExceptionsStatus()
    object NoUsersFound : PrivacySettingsDuelsExceptionsStatus()
    object Failure : PrivacySettingsDuelsExceptionsStatus()
    class Success(val privacySettingsDuelsExceptionsItems: List<PrivacyDuelsExceptionData>) :
        PrivacySettingsDuelsExceptionsStatus()
}

sealed class EditDuelsExceptionsStatus {
    object ServiceUnavailable : EditDuelsExceptionsStatus()
    object NoConnection : EditDuelsExceptionsStatus()
    object Failure : EditDuelsExceptionsStatus()
    object Success : EditDuelsExceptionsStatus()
}

sealed class HasFriendsStatus {
    object ServiceUnavailable : HasFriendsStatus()
    object NoConnection : HasFriendsStatus()
    object Failure : HasFriendsStatus()
    object HasFriends : HasFriendsStatus()
    object HasNoFriends : HasFriendsStatus()
}

sealed class GetNotificationsStatus {
    object ServiceUnavailable : GetNotificationsStatus()
    object NoConnection : GetNotificationsStatus()
    object Failure : GetNotificationsStatus()
    object NoNotificationsFound : GetNotificationsStatus()
    class Success(val notificationsData: ArrayList<NotificationData>) : GetNotificationsStatus()
}

sealed class CheckTokenIsCorrectStatus {
    object ServiceUnavailable : CheckTokenIsCorrectStatus()
    object NoConnection : CheckTokenIsCorrectStatus()
    object Failure : CheckTokenIsCorrectStatus()
    object Correct : CheckTokenIsCorrectStatus()
    object Waiting : CheckTokenIsCorrectStatus()
    object Incorrect : CheckTokenIsCorrectStatus()
}

sealed class SetNotificationsAreCheckedStatus {
    object ServiceUnavailable : SetNotificationsAreCheckedStatus()
    object NoConnection : SetNotificationsAreCheckedStatus()
    object Failure : SetNotificationsAreCheckedStatus()
    object Success : SetNotificationsAreCheckedStatus()
}

sealed class ExerciseReportStatus {
    object ServiceUnavailable : ExerciseReportStatus()
    object NoConnection : ExerciseReportStatus()
    object Failure : ExerciseReportStatus()
    object Success : ExerciseReportStatus()
}

sealed class InterestingLikeStatus {
    object ServiceUnavailable : InterestingLikeStatus()
    object NoConnection : InterestingLikeStatus()
    object Failure : InterestingLikeStatus()
    object Success : InterestingLikeStatus()
}

sealed class DownloadOfflineDataStatus{
    object ServiceUnavailable : DownloadOfflineDataStatus()
    object NoConnection : DownloadOfflineDataStatus()
    object IsCached : DownloadOfflineDataStatus()
    object Success : DownloadOfflineDataStatus()
    object Failure : DownloadOfflineDataStatus()
}

sealed class GetAchievementsStatus{
    object ServiceUnavailable : GetAchievementsStatus()
    object NoConnection : GetAchievementsStatus()
    object NoAchievements : GetAchievementsStatus()
    class Success(val achievements: List<AchievementData>) : GetAchievementsStatus()
    object Failure : GetAchievementsStatus()
}

sealed class GetAchievementsDataStatus{
    object ServiceUnavailable : GetAchievementsDataStatus()
    object NoConnection : GetAchievementsDataStatus()
    class Success(val achievements: List<AchievementDataData>) : GetAchievementsDataStatus()
    object Failure : GetAchievementsDataStatus()
}


