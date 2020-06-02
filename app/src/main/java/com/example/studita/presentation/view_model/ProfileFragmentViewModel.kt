package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.di.data.FriendsModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.CheckIsMyFriendStatus
import com.example.studita.domain.interactor.FriendActionStatus
import com.example.studita.domain.interactor.GetFriendsStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.utils.launchExt
import kotlinx.coroutines.*

class ProfileFragmentViewModel : ViewModel(){

    val friendsInteractor = FriendsModule.getFriendsInteractorImpl()
    val userDataInteractor = UserDataModule.getUserDataInteractorImpl()

    var addToFriendsJob: Job? = null

    val friendDataState = MutableLiveData<UserDataStatus>()

    val friendsState = MutableLiveData<GetFriendsStatus>()
    val isMyFriendState = MutableLiveData<CheckIsMyFriendStatus>()

    fun getProfileData(userId: Int, myId: Int){
        viewModelScope.launch{
            val friendDataDeferred = async { getFriendData(userId) }
            val friendsDeferred  = async { getFriends(userId) }
            val checkMyFriendDeferred = if(userId != myId) async { checkIsMyFriend(myId, userId) } else null
            val userDataStatus = friendDataDeferred.await()
            val getFriendsStatus = friendsDeferred.await()
            val checkMyFriendStatus = checkMyFriendDeferred?.await()
            if(userDataStatus is UserDataStatus.Success &&
                (getFriendsStatus is GetFriendsStatus.Success || getFriendsStatus is GetFriendsStatus.NoFriendsFound) &&
                (checkMyFriendStatus == null || checkMyFriendStatus == CheckIsMyFriendStatus.IsMyFriend || checkMyFriendStatus == CheckIsMyFriendStatus.IsMyFriend)) {
                friendDataState.postValue(userDataStatus)
                friendsState.postValue(getFriendsStatus)
                if (userId != myId)
                    isMyFriendState.postValue(checkMyFriendStatus!!)
            }
        }
    }

    private suspend fun getFriendData(userId: Int): UserDataStatus{
        return userDataInteractor.getUserData(userId, false)
    }

    private suspend fun checkIsMyFriend(userId: Int, anotherUserId: Int): CheckIsMyFriendStatus{
        return friendsInteractor.checkIsMyFriend(userId, anotherUserId)
    }

    private suspend fun getFriends(userId: Int): GetFriendsStatus{
            return friendsInteractor.getFriends(userId, 0, 3)
    }

    fun addToFriends(userIdToken: UserIdTokenData, friendId: Int){
        addToFriendsJob = viewModelScope.launchExt(addToFriendsJob){
            val result = friendsInteractor.addFriend(FriendActionRequestData(userIdToken, friendId))
            if(result is FriendActionStatus.Success)
                isMyFriendState.postValue(CheckIsMyFriendStatus.IsMyFriend)
        }
    }


    fun removeFromFriends(userIdToken: UserIdTokenData, friendId: Int){
        addToFriendsJob = viewModelScope.launchExt(addToFriendsJob){
            val result = friendsInteractor.removeFriend(FriendActionRequestData(userIdToken, friendId))
            if(result is FriendActionStatus.Success)
                isMyFriendState.postValue(CheckIsMyFriendStatus.IsNotMyFriend)
        }
    }

}