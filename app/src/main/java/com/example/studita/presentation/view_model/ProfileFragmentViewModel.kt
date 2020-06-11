package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.di.data.UsersModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.UserData
import com.example.studita.domain.entity.UsersResponseData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.CheckIsMyFriendStatus
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

    var friendsResponseData : UsersResponseData? = null

    val friendsState = MutableLiveData<GetUsersStatus>()
    val isMyFriendState = MutableLiveData<Boolean>()

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
                (checkMyFriendStatus == null || checkMyFriendStatus is CheckIsMyFriendStatus.IsMyFriend || checkMyFriendStatus is CheckIsMyFriendStatus.IsNotMyFriend)) {
                if(getFriendsStatus is GetUsersStatus.Success)
                    friendsResponseData = getFriendsStatus.friendsResponseData
                friendDataState.postValue(userDataStatus)
                friendsState.postValue(getFriendsStatus)
                if (friendOfUserId != myId)
                    isMyFriendState.postValue(checkMyFriendStatus is CheckIsMyFriendStatus.IsMyFriend)
            }
        }
    }

    private suspend fun getFriendData(userId: Int): UserDataStatus{
        return userDataInteractor.getUserData(userId, false)
    }

    private suspend fun checkIsMyFriend(userId: Int, anotherUserId: Int): CheckIsMyFriendStatus{
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
        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob){
            val result = friendsInteractor.addFriend(FriendActionRequestData(userIdToken, userData.userId))
            if(result is FriendActionStatus.Success) {
                isMyFriendState.postValue(true)
                UserUtils.isMyFriendLiveData.postValue(userData.apply {
                    isMyFriend = true
                })
            }
        }
    }


    fun removeFromFriends(userIdToken: UserIdTokenData, userData: UserData){
        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob){
            val result = friendsInteractor.removeFriend(FriendActionRequestData(userIdToken, userData.userId))
            if(result is FriendActionStatus.Success){
                isMyFriendState.postValue(false)
                UserUtils.isMyFriendLiveData.postValue(userData.apply {
                    isMyFriend = false
                })
            }
        }
    }

}