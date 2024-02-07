package com.codecoy.mvpflycollab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.AddPlaylistResponse
import com.codecoy.mvpflycollab.datamodels.AllPlaylistResponse
import com.codecoy.mvpflycollab.datamodels.UploadImageResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response

class PlaylistViewModel(private val mvpRepository: MvpRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var exceptionMutableLiveData = MutableLiveData<Throwable>()
    val exceptionLiveData: LiveData<Throwable> get() = exceptionMutableLiveData

    private val handler = CoroutineExceptionHandler { _, exception ->
        exceptionMutableLiveData.value = exception
    }

    private val allPlaylistResponseMutableLiveData = MutableLiveData<Response<AllPlaylistResponse>>()
    val allPlaylistResponseLiveData: LiveData<Response<AllPlaylistResponse>> get() = allPlaylistResponseMutableLiveData

    private val imageResponseMutableLiveData = MutableLiveData<Response<UploadImageResponse>>()
    val imageResponseLiveData: LiveData<Response<UploadImageResponse>> get() = imageResponseMutableLiveData

    private val addPlaylistResponseMutableLiveData = MutableLiveData<Response<AddPlaylistResponse>>()
    val addPlaylistResponseLiveData: LiveData<Response<AddPlaylistResponse>> get() = addPlaylistResponseMutableLiveData


    var selectedImage: String? = null
    var imagesList: MutableList<String> = arrayListOf()

    fun allPlayList(token: String,userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.allPlayList(token, userId)

            try {
                allPlaylistResponseMutableLiveData.value = response

            } catch (e: Exception) {
                allPlaylistResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun uploadImage(image: MultipartBody.Part){
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.uploadProfileImage(image)

            try {
                imageResponseMutableLiveData.value = response
            } catch (e: Exception) {
                imageResponseMutableLiveData.value = response
            }finally {
                _loading.value = false
            }
        }
    }

    fun addPlaylist(token: String, userId: String, title: String, description: String, playListImg: String){
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.addPlaylist(token, userId, title, description, playListImg)
            try {
                addPlaylistResponseMutableLiveData.value = response

            } catch (e: Exception) {
                addPlaylistResponseMutableLiveData.value = response
            }finally {
                _loading.value = false
            }
        }
    }
}