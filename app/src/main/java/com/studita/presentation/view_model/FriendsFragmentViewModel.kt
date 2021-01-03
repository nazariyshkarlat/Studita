package com.studita.presentation.view_model

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.App
import com.studita.R
import com.studita.di.data.UsersModule
import com.studita.domain.entity.FriendActionRequestData
import com.studita.domain.entity.UserData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.FriendActionStatus
import com.studita.domain.interactor.GetUsersStatus
import com.studita.domain.interactor.IsMyFriendStatus
import com.studita.domain.interactor.UserDataStatus
import com.studita.domain.interactor.users.UsersInteractor
import com.studita.domain.repository.UsersRepository
import com.studita.presentation.model.UsersRecyclerUiModel
import com.studita.presentation.model.toUserItemUiModel
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import com.studita.utils.launchExt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FriendsFragmentViewModel(private val userId: Int) : ViewModel() {

    private val friendsInteractor = UsersModule.getUsersInteractorImpl()
    val addFriendStatus = SingleLiveEvent<UsersInteractor.FriendActionState>()

    val errorEvent = SingleLiveEvent<Boolean>()
    val errorSnackbarEvent = SingleLiveEvent<Boolean>()
    var errorState = false

    var globalSearchOnly = false
    var searchState: SearchState = SearchState.NoSearch

    var recyclerItems: ArrayList<UsersRecyclerUiModel>? = null
    var userData: ArrayList<UserData>? = null

    var sortBy: UsersRepository.SortBy = friendsInteractor.defSortBy

    val progressState = MutableLiveData(true)

    val searchResultState = MutableLiveData<Pair<Boolean, SearchResultState>>()

    private var searchUsersJob: Job? = null

    var currentPageNumber = 1

    var startsWith: String? = null
    var isGlobalSearch: Boolean = false

    val perPage: Int = 20

    init {
        getUsers(
            userId,
            sortBy,
            false,
            null,
            false
        )
    }

    fun getUsers(
        profileId: Int,
        sortBy: UsersRepository.SortBy?,
        newPage: Boolean = false,
        startsWith: String? = null,
        isGlobalSearch: Boolean
    ) {

        errorState = false
        val isMyProfile = PrefsUtils.getUserId() == profileId

        if (newPage)
            currentPageNumber++
        else if(sortBy != this.sortBy || isGlobalSearch != this.isGlobalSearch || startsWith != this.startsWith) {
            currentPageNumber = 1
            progressState.value = true
        }

        this.startsWith = startsWith
        this.isGlobalSearch = isGlobalSearch

        if (sortBy != null)
            this.sortBy = sortBy

        searchUsersJob = viewModelScope.launchExt(searchUsersJob) {

            if(App.userDataDeferred.isCompleted && App.userDataDeferred.await() !is UserDataStatus.Success)
                App.authenticate(UserUtils.getUserIDTokenData(), true)

            var searchResultState: SearchResultState? = null
            val getUsersStatus = friendsInteractor.getUsers(
                if (isGlobalSearch) null else profileId,
                perPage,
                currentPageNumber,
                PrefsUtils.getUserId()!!,
                sortBy,
                startsWith
            )
            when (getUsersStatus) {
                is GetUsersStatus.Success -> {
                    when(App.userDataDeferred.await()) {
                        is UserDataStatus.Success -> {
                            searchResultState = if (currentPageNumber == 1) {
                                userData = ArrayList(getUsersStatus.friendsResponseData.users)
                                SearchResultState.ResultsFound(getUsersStatus.friendsResponseData.users)
                            } else {
                                userData?.addAll(getUsersStatus.friendsResponseData.users)
                                SearchResultState.MoreResultsFound(getUsersStatus.friendsResponseData.users)
                            }
                        }
                        is UserDataStatus.NoConnection -> {
                            errorEvent.value = true
                            errorState = true
                        }
                        else -> {
                            errorEvent.value = false
                            errorState = true
                        }
                    }
                }
                is GetUsersStatus.NoUsersFound -> {
                    when(App.userDataDeferred.await()) {
                        is UserDataStatus.Success -> {
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
                        is UserDataStatus.NoConnection -> {
                            errorEvent.value = true
                            errorState = true
                        }
                        else -> {
                            errorEvent.value = false
                            errorState = true
                        }
                    }
                }
                is GetUsersStatus.NoConnection -> {
                    errorEvent.value = true
                    errorState = true
                }
                is GetUsersStatus.ServiceUnavailable -> {
                    errorEvent.value = false
                    errorState = true
                }
            }

            if (searchResultState != null) {
                progressState.value = false
                this@FriendsFragmentViewModel.searchResultState.value =
                    (canBeMoreItems(searchResultState) to searchResultState)
            }
        }
    }


    fun formGlobalSearchEmptySearch() {
        searchUsersJob?.cancel()
        searchResultState.value =
            (searchResultState.value?.first == true) to SearchResultState.GlobalSearchEnterText
        isGlobalSearch = true
        startsWith = ""
        recyclerItems?.removeAll { it !is UsersRecyclerUiModel.SearchUiModel }
        progressState.value = false
    }

    private fun canBeMoreItems(searchResultState: SearchResultState) =
        (((searchResultState is SearchResultState.ResultsFound) && searchResultState.results.size == perPage) ||
                ((searchResultState is SearchResultState.MoreResultsFound) && searchResultState.results.size == perPage))

    fun removeFromFriends(userIdToken: UserIdTokenData, userData: UserData) {

        val newValue = if (userData.isMyFriendStatus is IsMyFriendStatus.Success.IsMyFriend)
            UsersInteractor.FriendActionState.RemovedFromFriends(
                userData.apply {
                    isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userId)
                })
        else
            UsersInteractor.FriendActionState.FriendshipRequestIsCanceled(
                userData.apply {
                    isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userId)
                })

        GlobalScope.launch(Dispatchers.Main) {
            val result = if (newValue is UsersInteractor.FriendActionState.RemovedFromFriends)
                friendsInteractor.removeFriend(
                    FriendActionRequestData(
                        userIdToken,
                        userData.userId
                    )
                )
            else
                friendsInteractor.cancelFriendship(
                    FriendActionRequestData(
                        userIdToken,
                        userData.userId
                    )
                )

            if (result is FriendActionStatus.Success)
                addFriendStatus.value = newValue
            else if(result is FriendActionStatus.ServiceUnavailable)
                errorSnackbarEvent.value = true
        }
        UserUtils.isMyFriendLiveData.value = newValue
    }

    fun addToFriends(userIdToken: UserIdTokenData, userData: UserData) {

        val newValue =
            if (userData.isMyFriendStatus is IsMyFriendStatus.Success.WaitingForFriendshipAccept)
                UsersInteractor.FriendActionState.FriendshipRequestIsAccepted(
                    userData.apply {
                        isMyFriendStatus = IsMyFriendStatus.Success.IsMyFriend(userId)
                    })
            else
                UsersInteractor.FriendActionState.FriendshipRequestIsSent(
                    userData.apply {
                        isMyFriendStatus = IsMyFriendStatus.Success.GotMyFriendshipRequest(userId)
                    })

        GlobalScope.launch(Dispatchers.Main) {
            val result =
                if (newValue is UsersInteractor.FriendActionState.FriendshipRequestIsAccepted)
                    friendsInteractor.acceptFriendship(
                        FriendActionRequestData(
                            userIdToken,
                            userData.userId
                        )
                    )
                else
                    friendsInteractor.sendFriendship(
                        FriendActionRequestData(
                            userIdToken,
                            userData.userId
                        )
                    )

            if (result is FriendActionStatus.Success)
                addFriendStatus.value = newValue
            else if(result is FriendActionStatus.ServiceUnavailable)
                errorSnackbarEvent.value = true
        }
        UserUtils.isMyFriendLiveData.value = newValue
    }

    fun friendsRequestIsPending() = searchUsersJob?.isActive == true

    fun getRecyclerItems(
        searchUiModel: UsersRecyclerUiModel.SearchUiModel,
        usersItems: List<UserData>,
        progressUiModel: UsersRecyclerUiModel.ProgressUiModel? = null
    ): List<UsersRecyclerUiModel> {
        val adapterItems = ArrayList<UsersRecyclerUiModel>()
        adapterItems.add(searchUiModel)
        adapterItems.addAll(usersItems.map { it.toUserItemUiModel() })
        if (progressUiModel != null)
            adapterItems.add(progressUiModel)
        return adapterItems
    }

    @StringRes
    fun getEmptyText() = when (searchResultState.value?.second) {
        is SearchResultState.GlobalSearchEnterText -> R.string.enter_name_to_search
        is SearchResultState.GlobalSearchNotFound -> R.string.global_search_no_results
        is SearchResultState.SearchFriendsNotFound -> R.string.friends_not_found
        else -> R.string.server_temporarily_unavailable
    }

    sealed class SearchState {
        object NoSearch : SearchState()
        data class FriendsSearch(var startsWith: String?) : SearchState()
        data class GlobalSearch(var startsWith: String?) : SearchState()
    }

    sealed class SearchResultState {
        object MyProfileEmptyFriends : SearchResultState()
        object SearchFriendsNotFound : SearchResultState()
        object GlobalSearchEnterText : SearchResultState()
        object GlobalSearchNotFound : SearchResultState()
        object NoMoreResults : SearchResultState()
        data class ResultsFound(val results: List<UserData>) : SearchResultState()
        data class MoreResultsFound(val results: List<UserData>) : SearchResultState()
    }

}