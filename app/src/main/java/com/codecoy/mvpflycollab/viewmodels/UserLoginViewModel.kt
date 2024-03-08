package com.codecoy.mvpflycollab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.UserLoginBody
import com.codecoy.mvpflycollab.datamodels.UserLoginResponse
import com.codecoy.mvpflycollab.repo.MvpRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Response

class UserLoginViewModel(private val mvpRepository: MvpRepository) : ViewModel() {


    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var exceptionMutableLiveData = MutableLiveData<Throwable>()
    val exceptionLiveData: LiveData<Throwable> get() = exceptionMutableLiveData

    private val handler = CoroutineExceptionHandler { _, exception ->
        exceptionMutableLiveData.value = exception
    }

    private val userLoginResponseMutableLiveData =
        MutableLiveData<Response<UserLoginResponse>>()

    val userLoginResponseLiveData: LiveData<Response<UserLoginResponse>>
        get()
    = userLoginResponseMutableLiveData


    fun userLogin(userLoginBody: UserLoginBody) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.userLogin(userLoginBody)

            try {
                _loading.value = false
                userLoginResponseMutableLiveData.value = response

            } catch (e: Exception) {
                _loading.value = false
                userLoginResponseMutableLiveData.value = response
            }
        }
    }
}