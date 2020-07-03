package com.example.studita.presentation.view_model

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.UsersModule
import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.UserData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.IsMyFriendStatus
import com.example.studita.domain.interactor.FriendActionStatus
import com.example.studita.domain.interactor.GetUsersStatus
import com.example.studita.domain.interactor.users.UsersInteractor
import com.example.studita.domain.repository.UsersRepository
import com.example.studita.presentation.model.UsersRecyclerUiModel
import com.example.studita.presentation.model.toUserItemUiModel
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FriendsFragmentViewModel : ViewModel(){

    private val friendsInteractor = UsersModule.getUsersInteractorImpl()
    val addFriendStatus = SingleLiveEvent<UsersInteractor.FriendActionState>()

    private var addToFriendsJob : Job? = null

    var globalSearchOnly = false
    var searchState: SearchState = SearchState.NoSearch

    var recyclerItems: ArrayList<UsersRecyclerUiModel>? = null
    var userData: ArrayList<UserData>? = null

    var sortBy: UsersRepository.SortBy = friendsInteractor.defSortBy

    val progressState = MutableLiveData(true)

    val searchResultState = MutableLiveData<Pair<Boolean, SearchResultState>>()

    var searchUsersJob : Job? = null

    var currentPageNumber = 1

    val perPage: Int = 20

    fun getUsers(friendOfUserId: Int, sortBy: UsersRepository.SortBy?, newPage: Boolean=false, startsWith: String? = null, isGlobalSearch: Boolean){

        val isMyProfile = PrefsUtils.getUserId() == friendOfUserId

        if(newPage)
            currentPageNumber++
        else
            currentPageNumber=1

        if(!newPage)
            progressState.value = true

        if(sortBy != null)
            this.sortBy = sortBy

        searchUsersJob = viewModelScope.launchExt(searchUsersJob) {
                var searchResultState: SearchResultState? = null
                val getUsersStatus = friendsInteractor.getUsers(
                    if (isGlobalSearch) null else friendOfUserId,
                    perPage,
                    currentPageNumber,
                    PrefsUtils.getUserId()!!,
                    sortBy,
                    startsWith
                )
                if (getUsersStatus is GetUsersStatus.Success) {
                    searchResultState = if (currentPageNumber == 1) {
                        userData = ArrayList(getUsersStatus.friendsResponseData.users)
                        SearchResultState.ResultsFound(getUsersStatus.friendsResponseData.users)
                    }else {
                        userData?.addAll(getUsersStatus.friendsResponseData.users)
                        SearchResultState.MoreResultsFound(getUsersStatus.friendsResponseData.users)
                    }
                } else if (getUsersStatus is GetUsersStatus.NoUsersFound) {
                    if (!isGlobalSearch) {
                        if (startsWith != null) {
                            searchResultState = if (currentPageNumber == 1)
                                SearchResultState.SearchFriendsNotFound
                            else
                                SearchResultState.NoMoreResults
                        } else {
                            if (currentPageNumber == 1) {
                                if (isMyProfile)
                                    searchResultState = SearchResultState.MyProfileEmptyFriends
                            } else {
                                searchResultState = SearchResultState.NoMoreResults
                            }
                        }
                    } else {
                        searchResultState = if (currentPageNumber == 1)
                            SearchResultState.GlobalSearchNotFound
                        else
                            SearchResultState.NoMoreResults

                    }
                }

                if (searchResultState != null) {
                    progressState.value = false
                    this@FriendsFragmentViewModel.searchResultState.postValue(
                        (canBeMoreItems(searchResultState) to searchResultState))
                }
            }
    }

    private fun canBeMoreItems(searchResultState: SearchResultState) = (((searchResultState is SearchResultState.ResultsFound) && searchResultState.results.size == perPage) ||
            ((searchResultState is SearchResultState.MoreResultsFound) && searchResultState.results.size == perPage))

    fun removeFromFriends(userIdToken: UserIdTokenData, userData: UserData){
        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob){
            val result = friendsInteractor.removeFriend(FriendActionRequestData(userIdToken, userData.userId))
            if(result == FriendActionStatus.Success) {
                if(userData.isMyFriendStatus is IsMyFriendStatus.Success.IsMyFriend)
                    addFriendStatus.postValue(UsersInteractor.FriendActionState.RemovedFromFriends(userData))
                else
                    addFriendStatus.postValue(UsersInteractor.FriendActionState.MyFriendshipRequestIsCanceled(userData))
            }
            UserUtils.isMyFriendLiveData.postValue(userData.apply {
                isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userId)
            })
        }
    }

    fun addToFriends(userIdToken: UserIdTokenData, userData: UserData){
        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob){
            val result = if(userData.isMyFriendStatus is IsMyFriendStatus.Success.WaitingForFriendshipAccept)
                friendsInteractor.acceptFriendship(FriendActionRequestData(userIdToken, userData.userId))
            else
                friendsInteractor.addFriend(FriendActionRequestData(userIdToken, userData.userId))
            if(result == FriendActionStatus.Success) {
                if(userData.isMyFriendStatus is IsMyFriendStatus.Success.WaitingForFriendshipAccept)
                    addFriendStatus.postValue(UsersInteractor.FriendActionState.AddedToFriends(userData))
                else
                    addFriendStatus.postValue(UsersInteractor.FriendActionState.FriendshipRequestIsSent(userData))
            }
            UserUtils.isMyFriendLiveData.postValue(userData.apply {
                isMyFriendStatus = if(isMyFriendStatus is IsMyFriendStatus.Success.WaitingForFriendshipAccept)
                    IsMyFriendStatus.Success.IsMyFriend(userId)
                else
                    IsMyFriendStatus.Success.GotMyFriendshipRequest(userId)
            }
            )
        }
    }

    fun getRecyclerItems(searchUiModel: UsersRecyclerUiModel.SearchUiModel, usersItems: List<UserData>, progressUiModel: UsersRecyclerUiModel.ProgressUiModel? = null): List<UsersRecyclerUiModel>{
        val adapterItems = ArrayList<UsersRecyclerUiModel>()
        adapterItems.add(searchUiModel)
        adapterItems.addAll(usersItems.map { it.toUserItemUiModel() })
        if(progressUiModel != null)
            adapterItems.add(progressUiModel)
        return adapterItems
    }

    @StringRes
    fun getEmptyText() = when(searchResultState.value?.second){
        is SearchResultState.GlobalSearchEnterText -> R.string.enter_name_to_search
        is SearchResultState.GlobalSearchNotFound -> R.string.global_search_no_results
        is SearchResultState.SearchFriendsNotFound -> R.string.friends_not_found
        else -> R.string.server_failure
    }

    sealed class SearchState{
        object NoSearch: SearchState()
        data class FriendsSearch(var startsWith: String?): SearchState()
        data class GlobalSearch(var startsWith: String?): SearchState()
    }

    sealed class SearchResultState{
        object MyProfileEmptyFriends: SearchResultState()
        object SearchFriendsNotFound : SearchResultState()
        object GlobalSearchEnterText: SearchResultState()
        object GlobalSearchNotFound: SearchResultState()
        object NoMoreResults : SearchResultState()
        data class ResultsFound(val results: List<UserData>) : SearchResultState()
        data class MoreResultsFound(val results: List<UserData>) : SearchResultState()
    }

}