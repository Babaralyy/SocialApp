package com.codecoy.mvpflycollab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.AllJourneyResponse
import com.codecoy.mvpflycollab.datamodels.AllUserResponse
import com.codecoy.mvpflycollab.datamodels.FollowUserResponse
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

    fun allUsers(token: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.allUsers(token)

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

}