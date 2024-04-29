package com.codecoy.mvpflycollab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.AcceptColReqResponse
import com.codecoy.mvpflycollab.datamodels.AcceptReqResponse
import com.codecoy.mvpflycollab.datamodels.AllJourneyResponse
import com.codecoy.mvpflycollab.datamodels.AllUserResponse
import com.codecoy.mvpflycollab.datamodels.CollaboratorsListResponse
import com.codecoy.mvpflycollab.datamodels.DeclineReqResponse
import com.codecoy.mvpflycollab.datamodels.FollowRequestsResponse
import com.codecoy.mvpflycollab.datamodels.FollowUserResponse
import com.codecoy.mvpflycollab.datamodels.UserColResponse
import com.codecoy.mvpflycollab.datamodels.UserFollowingResponse
import com.codecoy.mvpflycollab.datamodels.UserPostsResponse
import com.codecoy.mvpflycollab.datamodels.UserProfileResponse
import com.codecoy.mvpflycollab.repo.MvpRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Response

class UserViewModel(private val mvpRepository: MvpRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var exceptionMutableLiveData = MutableLiveData<Throwable>()
    val exceptionLiveData: LiveData<Throwable> get() = exceptionMutableLiveData

    private val handler = CoroutineExceptionHandler { _, exception ->
        exceptionMutableLiveData.value = exception
    }


    private val allUsersResponseMutableLiveData = MutableLiveData<Response<AllUserResponse>>()
    val allUsersResponseLiveData: LiveData<Response<AllUserResponse>> get() = allUsersResponseMutableLiveData

    private val followUsersResponseMutableLiveData = MutableLiveData<Response<FollowUserResponse>>()
    val followUsersResponseLiveData: LiveData<Response<FollowUserResponse>> get() = followUsersResponseMutableLiveData

    private val usersProfileResponseMutableLiveData = MutableLiveData<Response<UserProfileResponse>>()
    val usersProfileResponseLiveData: LiveData<Response<UserProfileResponse>> get() = usersProfileResponseMutableLiveData

    private val usersFollowingResponseMutableLiveData = MutableLiveData<Response<UserFollowingResponse>>()
    val usersFollowingProfileResponseLiveData: LiveData<Response<UserFollowingResponse>> get() = usersFollowingResponseMutableLiveData

    private val collabUsersResponseMutableLiveData = MutableLiveData<Response<UserColResponse>>()
    val collabUsersResponseLiveData: LiveData<Response<UserColResponse>> get() = collabUsersResponseMutableLiveData

    private val usersFollowingReqResponseMutableLiveData = MutableLiveData<Response<FollowRequestsResponse>>()
    val usersFollowingReqProfileResponseLiveData: LiveData<Response<FollowRequestsResponse>> get() = usersFollowingReqResponseMutableLiveData

    private val usersColReqResponseMutableLiveData = MutableLiveData<Response<FollowRequestsResponse>>()
    val usersColReqProfileResponseLiveData: LiveData<Response<FollowRequestsResponse>> get() = usersColReqResponseMutableLiveData

    private val usersColResponseMutableLiveData = MutableLiveData<Response<CollaboratorsListResponse>>()
    val usersColResponseLiveData: LiveData<Response<CollaboratorsListResponse>> get() = usersColResponseMutableLiveData

    private val acceptFollowReqMutableLiveData = MutableLiveData<Response<AcceptReqResponse>>()
    val acceptFollowReqLiveData: LiveData<Response<AcceptReqResponse>> get() = acceptFollowReqMutableLiveData

    private val acceptColReqMutableLiveData = MutableLiveData<Response<AcceptColReqResponse>>()
    val acceptColReqLiveData: LiveData<Response<AcceptColReqResponse>> get() = acceptColReqMutableLiveData

    private val declineFollowReqMutableLiveData = MutableLiveData<Response<DeclineReqResponse>>()
    val declineFollowReqLiveData: LiveData<Response<DeclineReqResponse>> get() = declineFollowReqMutableLiveData

    private val declineColReqMutableLiveData = MutableLiveData<Response<DeclineReqResponse>>()
    val declineColReqLiveData: LiveData<Response<DeclineReqResponse>> get() = declineColReqMutableLiveData

    fun allUsers(token: String, userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.allUsers(token, userId)

            try {
                allUsersResponseMutableLiveData.value = response

            } catch (e: Exception) {
                allUsersResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun followUser(
        token: String,
        userId: String,
        followerId: String,
        date: String,
        time: String
    ) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.followUser(token, userId, followerId, date, time)

            try {
                followUsersResponseMutableLiveData.value = response

            } catch (e: Exception) {
                followUsersResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun userProfile(token: String, userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.userProfile(token, userId)

            try {
                usersProfileResponseMutableLiveData.value = response

            } catch (e: Exception) {
                usersProfileResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }


    fun userFollowing(token: String, userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.userFollowing(token, userId)

            try {
                usersFollowingResponseMutableLiveData.value = response

            } catch (e: Exception) {
                usersFollowingResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }


    fun collabUser(
        token: String,
        userId: String,
        collabId: String,
        date: String,
        time: String
    ) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.collabUser(token, userId, collabId, date, time)

            try {
                collabUsersResponseMutableLiveData.value = response

            } catch (e: Exception) {
                collabUsersResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun userFollowingRequests(token: String, userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.userFollowingRequests(token, userId)

            try {
                usersFollowingReqResponseMutableLiveData.value = response

            } catch (e: Exception) {
                usersFollowingReqResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun userColRequests(token: String, userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.userCollabRequests(token, userId)

            try {
                usersColReqResponseMutableLiveData.value = response

            } catch (e: Exception) {
                usersColReqResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun collaboratorsList(token: String, userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.collaboratorsList(token, userId)

            try {
                usersColResponseMutableLiveData.value = response

            } catch (e: Exception) {
                usersColResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun acceptFollowRequest(
        token: String,
        userId: String,
        followerRequestId: String,
        date: String,
        time: String
    ) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.acceptFollowRequest(token, userId, followerRequestId, date, time)

            try {
                acceptFollowReqMutableLiveData.value = response

            } catch (e: Exception) {
                acceptFollowReqMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun acceptColRequest(
        token: String,
        userId: String,
        colRequestId: String,
        date: String,
        time: String
    ) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.acceptColRequest(token, userId, colRequestId, date, time)

            try {
                acceptColReqMutableLiveData.value = response

            } catch (e: Exception) {
                acceptColReqMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun declineFollowReq(token: String, userId: String, followerId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.declineFollowReq(token, userId, followerId)

            try {
                declineFollowReqMutableLiveData.value = response

            } catch (e: Exception) {
                declineFollowReqMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun declineColReq(token: String, userId: String, colId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.declineColReq(token, userId, colId)

            try {
                declineColReqMutableLiveData.value = response

            } catch (e: Exception) {
                declineColReqMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }



}