package com.codecoy.mvpflycollab.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailResponse
import com.codecoy.mvpflycollab.datamodels.AddPlaylistDetailsResponse
import com.codecoy.mvpflycollab.datamodels.AddPlaylistResponse
import com.codecoy.mvpflycollab.datamodels.AllPlaylistDetailsResponse
import com.codecoy.mvpflycollab.datamodels.AllPlaylistResponse
import com.codecoy.mvpflycollab.datamodels.UploadImageResponse
import com.codecoy.mvpflycollab.utils.Constant.TAG
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
        Log.i(TAG, "addNewPlaylistDetail:: value $exception")
    }

    private val allPlaylistResponseMutableLiveData = MutableLiveData<Response<AllPlaylistResponse>>()
    val allPlaylistResponseLiveData: LiveData<Response<AllPlaylistResponse>> get() = allPlaylistResponseMutableLiveData

    private val imageResponseMutableLiveData = MutableLiveData<Response<UploadImageResponse>>()
    val imageResponseLiveData: LiveData<Response<UploadImageResponse>> get() = imageResponseMutableLiveData

    private val addPlaylistResponseMutableLiveData = MutableLiveData<Response<AddPlaylistResponse>>()
    val addPlaylistResponseLiveData: LiveData<Response<AddPlaylistResponse>> get() = addPlaylistResponseMutableLiveData

    private val allPlaylistDetailsResponseMutableLiveData = MutableLiveData<Response<AllPlaylistDetailsResponse>>()
    val allPlaylistDetailsResponseLiveData: LiveData<Response<AllPlaylistDetailsResponse>> get() = allPlaylistDetailsResponseMutableLiveData

    private val addPlaylistDetailsMutableLiveData = MutableLiveData<Response<AddPlaylistDetailsResponse>>()
    val addPlaylistDetailsLiveData: LiveData<Response<AddPlaylistDetailsResponse>> get() = addPlaylistDetailsMutableLiveData


    var selectedImage: String? = null
    var imagesList: MutableList<String> = arrayListOf()
    var videoList: MutableList<Uri> = arrayListOf()

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

    fun allPlaylistDetailsList(token: String, journeyId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.allPlaylistDetailsList(token, journeyId)

            try {
                allPlaylistDetailsResponseMutableLiveData.value = response

            } catch (e: Exception) {

                allPlaylistDetailsResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

     fun addPlaylistDetails(token: String, playlistId: String, title: String, description: String, date: String, videosPartList: MutableList<MultipartBody.Part>){
         viewModelScope.launch(handler) {
             _loading.value = true
             val response = mvpRepository.addPlaylistDetails(token, playlistId, title, description, date, videosPartList)

             try {
                 Log.i(TAG, "addNewPlaylistDetail:: $response")
                 addPlaylistDetailsMutableLiveData.value = response

             } catch (e: Exception) {
                 Log.i(TAG, "addNewPlaylistDetail:: Exception $response")
                 addPlaylistDetailsMutableLiveData.value = response
             } finally {
                 _loading.value = false
             }
         }
     }

}