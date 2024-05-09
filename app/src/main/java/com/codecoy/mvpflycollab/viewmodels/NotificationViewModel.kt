package com.codecoy.mvpflycollab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.NotificationResponse
import com.codecoy.mvpflycollab.repo.MvpRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Response

class NotificationViewModel(private val mvpRepository: MvpRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var exceptionMutableLiveData = MutableLiveData<Throwable>()
    val exceptionLiveData: LiveData<Throwable> get() = exceptionMutableLiveData

    private val handler = CoroutineExceptionHandler { _, exception ->
        exceptionMutableLiveData.value = exception
    }


    private val notListResponseMutableLiveData = MutableLiveData<Response<NotificationResponse>>()
    val notListResponseLiveData: LiveData<Response<NotificationResponse>> get() = notListResponseMutableLiveData


    fun notifications(token: String,userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.notifications(token, userId)

            try {
                notListResponseMutableLiveData.value = response

            } catch (e: Exception) {
                notListResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

}