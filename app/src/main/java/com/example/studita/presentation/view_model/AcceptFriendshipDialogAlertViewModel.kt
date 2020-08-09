package com.example.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import com.example.studita.di.data.UsersModule
import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.UserData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.FriendActionStatus
import com.example.studita.domain.interactor.IsMyFriendStatus
import com.example.studita.domain.interactor.users.UsersInteractor
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job

class AcceptFriendshipDialogAlertViewModel : ViewModel() {

    private val friendsInteractor = UsersModule.getUsersInteractorImpl()
    val addFriendStatus = SingleLiveEvent<UsersInteractor.FriendActionState>()

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
                addFriendStatus.postValue(
                    UsersInteractor.FriendActionState.FriendshipRequestIsAccepted(
                        userData.apply {
                            isMyFriendStatus = IsMyFriendStatus.Success.IsMyFriend(userId)
                        })
                )
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
                addFriendStatus.postValue(
                    UsersInteractor.FriendActionState.FriendshipRequestIsRejected(
                        userData.apply {
                            isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userId)
                        })
                )
        }
        UserUtils.isMyFriendLiveData.value =
            UsersInteractor.FriendActionState.FriendshipRequestIsRejected(userData.apply {
                isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userId)
            })
    }


}