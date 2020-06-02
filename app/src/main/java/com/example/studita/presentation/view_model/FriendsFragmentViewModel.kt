package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.di.data.FriendsModule
import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.CheckIsMyFriendStatus
import com.example.studita.domain.interactor.FriendActionStatus
import com.example.studita.domain.interactor.GetFriendsStatus
import com.example.studita.presentation.model.FriendsRecyclerUiModel
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FriendsFragmentViewModel : ViewModel(){

    val friendsInteractor = FriendsModule.getFriendsInteractorImpl()
    var job: Job? = null
    val removeFragmentStatus = SingleLiveEvent<Int>()

    val friendsState = MutableLiveData<GetFriendsStatus>()

    init {
        UserUtils.userData.userId?.let { getFriends(it) }
    }

    fun getFriends(userId: Int){
        job = viewModelScope.launchExt(job){
            friendsState.postValue(friendsInteractor.getFriends(userId, 0, 20))
        }
    }

    fun removeFromFriends(userIdToken: UserIdTokenData, friendId: Int){
        viewModelScope.launch{
            val result = friendsInteractor.removeFriend(FriendActionRequestData(userIdToken, friendId))
            if(result == FriendActionStatus.Success)
                removeFragmentStatus.postValue(friendId)
        }
    }

    fun getRecyclerItems(searchUiModel: FriendsRecyclerUiModel.SearchUiModel, friendsItems: List<FriendsRecyclerUiModel>): List<FriendsRecyclerUiModel>{
        val adapterItems = ArrayList<FriendsRecyclerUiModel>()
        adapterItems.add(searchUiModel)
        adapterItems.addAll(friendsItems)
        return adapterItems
    }

}