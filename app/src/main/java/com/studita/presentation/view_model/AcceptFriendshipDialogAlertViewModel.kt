package com.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import com.studita.di.data.UsersModule
import com.studita.domain.entity.FriendActionRequestData
import com.studita.domain.entity.UserData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.FriendActionStatus
import com.studita.domain.interactor.IsMyFriendStatus
import com.studita.domain.interactor.users.UsersInteractor
import com.studita.utils.UserUtils
import com.studita.utils.launchExt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job

class AcceptFriendshipDialogAlertViewModel : ViewModel() {

    private val friendsInteractor = UsersModule.getUsersInteractorImpl()
    val addFriendStatus = SingleLiveEvent<UsersInteractor.FriendActionState>()
    val errorEvent = SingleLiveEvent<Boolean>()

    private var addToFriendsJob: Job? = null

    fun acceptFriendshipRequest(userIdToken: UserIdTokenData, userData: UserData) {
        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob) {
            val result = friendsInteractor.acceptFriendship(
                FriendActionRequestData(
                    userIdToken,
                    userData.userId
                )
            )
            if (result is FriendActionStatus.Success)
                addFriendStatus.value =
                    UsersInteractor.FriendActionState.FriendshipRequestIsAccepted(
                        userData.apply {
                            isMyFriendStatus = IsMyFriendStatus.Success.IsMyFriend(userId)
                        })
            else if(result is FriendActionStatus.ServiceUnavailable || result is FriendActionStatus.Failure){
                errorEvent.value = true
            }
        }
        UserUtils.isMyFriendLiveData.value =
            UsersInteractor.FriendActionState.FriendshipRequestIsAccepted(userData.apply {
                isMyFriendStatus = IsMyFriendStatus.Success.IsMyFriend(userId)
            })
    }

    fun rejectFriendshipRequest(userIdToken: UserIdTokenData, userData: UserData) {
        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob) {
            val result = friendsInteractor.rejectFriendship(
                FriendActionRequestData(
                    userIdToken,
                    userData.userId
                )
            )
            if (result is FriendActionStatus.Success)
                addFriendStatus.value =
                    UsersInteractor.FriendActionState.FriendshipRequestIsRejected(
                        userData.apply {
                            isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userId)
                        })
            else if(result is FriendActionStatus.ServiceUnavailable || result is FriendActionStatus.Failure)
                errorEvent.value = true
        }
        UserUtils.isMyFriendLiveData.value =
            UsersInteractor.FriendActionState.FriendshipRequestIsRejected(userData.apply {
                isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userId)
            })
    }


}