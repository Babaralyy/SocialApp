package com.codecoy.mvpflycollab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.AddCommentResponse
import com.codecoy.mvpflycollab.datamodels.CommentsResponse
import com.codecoy.mvpflycollab.repo.MvpRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Response

class CommentsViewModel(private val mvpRepository: MvpRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var exceptionMutableLiveData = MutableLiveData<Throwable>()
    val exceptionLiveData: LiveData<Throwable> get() = exceptionMutableLiveData

    private val handler = CoroutineExceptionHandler { _, exception ->
        exceptionMutableLiveData.value = exception
    }


    private val allCommentsResponseMutableLiveData = MutableLiveData<Response<CommentsResponse>>()
    val allCommentsResponseLiveData: LiveData<Response<CommentsResponse>> get() = allCommentsResponseMutableLiveData

    private val addCommentsResponseMutableLiveData = MutableLiveData<Response<AddCommentResponse>>()
    val addCommentsResponseLiveData: LiveData<Response<AddCommentResponse>> get() = addCommentsResponseMutableLiveData
    fun allCommentsAgainstPost(token: String, postId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.allCommentsAgainstPost(token, postId)

            try {
                allCommentsResponseMutableLiveData.value = response

            } catch (e: Exception) {
                allCommentsResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

     fun addComment(
        token: String,
        userId: String,
        postId: String,
        commentTitle: String,
        date: String,
        time: String
    ) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.addComment(token, userId, postId, commentTitle, date, time)

            try {
                addCommentsResponseMutableLiveData.value = response

            } catch (e: Exception) {
                addCommentsResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }
}