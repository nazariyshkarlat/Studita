package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.di.data.UsersModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.*
import com.example.studita.domain.interactor.IsMyFriendStatus
import com.example.studita.domain.interactor.FriendActionStatus
import com.example.studita.domain.interactor.GetUsersStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.*

class ProfileFragmentViewModel : ViewModel(){

    private val friendsInteractor = UsersModule.getUsersInteractorImpl()
    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()

    var addToFriendsJob: Job? = null
    var getFriendsJob: Job? = null

    val friendDataState = MutableLiveData<UserDataStatus>()

    val friendsState = MutableLiveData<GetUsersStatus>()
    val isMyFriendState = MutableLiveData<IsMyFriendStatus>()

    fun getProfileData(myId: Int, friendOfUserId: Int){
        viewModelScope.launch{
            val friendDataDeferred = async { getFriendData(friendOfUserId) }
            val friendsDeferred  = async {  getFriendsAsync(friendOfUserId) }
            val checkMyFriendDeferred = if(friendOfUserId != myId) async { checkIsMyFriend(myId, friendOfUserId) } else null
            val userDataStatus = friendDataDeferred.await()
            val getFriendsStatus = friendsDeferred.await()
            val checkMyFriendStatus = checkMyFriendDeferred?.await()
            if(userDataStatus is UserDataStatus.Success &&
                (getFriendsStatus is GetUsersStatus.Success || getFriendsStatus is GetUsersStatus.NoUsersFound) &&
                (checkMyFriendStatus == null || checkMyFriendStatus is IsMyFriendStatus.Success)) {
                friendDataState.postValue(userDataStatus)
                friendsState.postValue(getFriendsStatus)
                if (friendOfUserId != myId)
                    isMyFriendState.postValue(checkMyFriendStatus)
            }
        }
    }

    private suspend fun getFriendData(userId: Int): UserDataStatus{
        return userDataInteractor.getUserData(userId, false)
    }

    private suspend fun checkIsMyFriend(userId: Int, anotherUserId: Int): IsMyFriendStatus{
        return friendsInteractor.checkIsMyFriend(userId, anotherUserId)
    }

    private suspend fun getFriendsAsync(userId: Int): GetUsersStatus{
            return friendsInteractor.getUsers(userId, 3, 1, sortBy = friendsInteractor.defSortBy, userId = PrefsUtils.getUserId()!!)
    }

    fun getFriends(userId: Int){
        getFriendsJob = viewModelScope.launchExt(getFriendsJob){
            val status = friendsInteractor.getUsers(userId, 3, 1, sortBy = friendsInteractor.defSortBy, userId = PrefsUtils.getUserId()!!)
            if(status is GetUsersStatus.Success)
                friendsState.postValue(status)
        }
    }

    fun addToFriends(userIdToken: UserIdTokenData, userData: UserData){
        UserUtils.isMyFriendLiveData.value = userData.apply {
            isMyFriendStatus = IsMyFriendStatus.Success.GotMyFriendshipRequest(userId)
        }
        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob){
            friendsInteractor.addFriend(FriendActionRequestData(userIdToken, userData.userId))
        }
    }

    fun acceptFriendshipRequest(userIdToken: UserIdTokenData, userData: UserData){
        UserUtils.isMyFriendLiveData.value = userData.apply {
            isMyFriendStatus = IsMyFriendStatus.Success.IsMyFriend(userId)
        }
        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob){
            friendsInteractor.acceptFriendship(FriendActionRequestData(userIdToken, userData.userId))
        }
    }


    fun removeFromFriends(userIdToken: UserIdTokenData, userData: UserData){
        UserUtils.isMyFriendLiveData.value = userData.apply {
            isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userId)
        }
        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob){
            friendsInteractor.removeFriend(FriendActionRequestData(userIdToken, userData.userId))
        }
    }

}