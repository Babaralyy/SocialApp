package com.codecoy.mvpflycollab.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.OneTwoOneChatResponse
import com.codecoy.mvpflycollab.datamodels.UserChatListResponse
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.utils.Constant
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Response

class ChatViewModel(private val mvpRepository: MvpRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var exceptionMutableLiveData = MutableLiveData<Throwable>()
    val exceptionLiveData: LiveData<Throwable> get() = exceptionMutableLiveData

    private val handler = CoroutineExceptionHandler { _, exception ->
        exceptionMutableLiveData.value = exception
        Log.i(Constant.TAG, "addNewPlaylistDetail:: value $exception")
    }

    private val chatListResponseMutableLiveData = MutableLiveData<Response<UserChatListResponse>>()
    val chatListResponseLiveData: LiveData<Response<UserChatListResponse>> get() = chatListResponseMutableLiveData

    private val oneTwoOneResponseMutableLiveData = MutableLiveData<Response<OneTwoOneChatResponse>>()
    val oneTwoOneResponseLiveData: LiveData<Response<OneTwoOneChatResponse>> get() = oneTwoOneResponseMutableLiveData

    fun userChatList(token: String,userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.userChatList(token, userId)

            try {
                chatListResponseMutableLiveData.value = response

            } catch (e: Exception) {
                chatListResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun oneTwoOneChat(token: String, senderId: String, receiverId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.oneTwoOneChat(token, senderId, receiverId)

            try {
                oneTwoOneResponseMutableLiveData.value = response

            } catch (e: Exception) {
                oneTwoOneResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }
}