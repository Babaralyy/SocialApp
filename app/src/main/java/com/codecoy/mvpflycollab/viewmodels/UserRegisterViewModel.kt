package com.codecoy.mvpflycollab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.UploadImageResponse
import com.codecoy.mvpflycollab.datamodels.UserRegisterBody
import com.codecoy.mvpflycollab.datamodels.UserRegisterResponse
import com.codecoy.mvpflycollab.repo.MvpRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
class UserRegisterViewModel(private val mvpRepository: MvpRepository) : ViewModel() {

     var selectedImage: String? = null

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var exceptionMutableLiveData = MutableLiveData<Throwable>()
    val exceptionLiveData: LiveData<Throwable> get() = exceptionMutableLiveData

    private val handler = CoroutineExceptionHandler { _, exception ->
        exceptionMutableLiveData.value = exception
    }

    private val userRegisterResponseMutableLiveData =
        MutableLiveData<Response<UserRegisterResponse>>()
    private val profileImageResponseMutableLiveData =
        MutableLiveData<Response<UploadImageResponse>>()

    val userRegisterResponseLiveData: LiveData<Response<UserRegisterResponse>> get()
    = userRegisterResponseMutableLiveData
    val profileImageResponseLiveData: LiveData<Response<UploadImageResponse>> get()
    = profileImageResponseMutableLiveData


    fun uploadProfileImage(image: MultipartBody.Part){
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.uploadProfileImage(image)

            try {
                _loading.value = false
                profileImageResponseMutableLiveData.value = response

            } catch (e: Exception) {
                _loading.value = false
                profileImageResponseMutableLiveData.value = response
            }
        }
    }

    fun registerUser(userRegisterBody: UserRegisterBody) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.userRegister(userRegisterBody)

            try {
                _loading.value = false
                userRegisterResponseMutableLiveData.value = response

            } catch (e: Exception) {
                _loading.value = false
                userRegisterResponseMutableLiveData.value = response
            }
        }
    }
}