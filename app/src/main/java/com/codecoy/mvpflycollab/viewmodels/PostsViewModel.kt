package com.codecoy.mvpflycollab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.UserPostsResponse
import com.codecoy.mvpflycollab.repo.MvpRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Response

class PostsViewModel(private val mvpRepository: MvpRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var exceptionMutableLiveData = MutableLiveData<Throwable>()
    val exceptionLiveData: LiveData<Throwable> get() = exceptionMutableLiveData

    private val handler = CoroutineExceptionHandler { _, exception ->
        exceptionMutableLiveData.value = exception
    }

    private val allUsersPostsResponseMutableLiveData = MutableLiveData<Response<UserPostsResponse>>()
    val allUsersPostsResponseLiveData: LiveData<Response<UserPostsResponse>> get() = allUsersPostsResponseMutableLiveData


    fun allUserPosts(token: String, userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.allUserPosts(token, userId)

            try {
                allUsersPostsResponseMutableLiveData.value = response

            } catch (e: Exception) {
                allUsersPostsResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

}