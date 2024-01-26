package com.codecoy.mvpflycollab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailBody
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailResponse
import com.codecoy.mvpflycollab.datamodels.AddJourneyResponse
import com.codecoy.mvpflycollab.datamodels.AllJourneyResponse
import com.codecoy.mvpflycollab.datamodels.UploadImageResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response

class JourneyViewModel(private val mvpRepository: MvpRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var exceptionMutableLiveData = MutableLiveData<Throwable>()
    val exceptionLiveData: LiveData<Throwable> get() = exceptionMutableLiveData

    private val handler = CoroutineExceptionHandler { _, exception ->
        exceptionMutableLiveData.value = exception
    }


    var selectedImage: String? = null
    var imagesList: MutableList<String> = arrayListOf()

    private val allJourneyResponseMutableLiveData = MutableLiveData<Response<AllJourneyResponse>>()
    val allJourneyResponseLiveData: LiveData<Response<AllJourneyResponse>> get() = allJourneyResponseMutableLiveData

    private val profileImageResponseMutableLiveData = MutableLiveData<Response<UploadImageResponse>>()
    val profileImageResponseLiveData: LiveData<Response<UploadImageResponse>> get() = profileImageResponseMutableLiveData


    private val imageResponseMutableLiveData = MutableLiveData<Response<UploadImageResponse>>()
    val imageResponseLiveData: LiveData<Response<UploadImageResponse>> get() = imageResponseMutableLiveData


    private val addJourneyResponseMutableLiveData = MutableLiveData<Response<AddJourneyResponse>>()
    val addJourneyResponseLiveData: LiveData<Response<AddJourneyResponse>> get() = addJourneyResponseMutableLiveData


    private val addJourneyDetailMutableLiveData = MutableLiveData<Response<AddJourneyDetailResponse>>()
    val addJourneyDetailLiveData: LiveData<Response<AddJourneyDetailResponse>> get() = addJourneyDetailMutableLiveData

    fun allJourneyList(token: String,userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.allJourneyList(token, userId)

            try {
                _loading.value = false
                allJourneyResponseMutableLiveData.value = response

            } catch (e: Exception) {
                _loading.value = false
                allJourneyResponseMutableLiveData.value = response
            }
        }
    }

    fun uploadImage(image: MultipartBody.Part){
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.uploadProfileImage(image)

            try {
                _loading.value = false
                imageResponseMutableLiveData.value = response

            } catch (e: Exception) {
                _loading.value = false
                imageResponseMutableLiveData.value = response
            }
        }
    }

    fun addJourney(token: String, userId: String, title: String, description: String, journeyImg: String){
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.addJourney(token, userId, title, description, journeyImg)

            try {
                _loading.value = false
                addJourneyResponseMutableLiveData.value = response

            } catch (e: Exception) {
                _loading.value = false
                addJourneyResponseMutableLiveData.value = response
            }
        }
    }

     fun addJourneyDetail(token: String, addJourneyDetailBody: AddJourneyDetailBody){
         viewModelScope.launch(handler) {
             _loading.value = true
             val response = mvpRepository.addJourneyDetail(token, addJourneyDetailBody)

             try {
                 _loading.value = false
                 addJourneyDetailMutableLiveData.value = response

             } catch (e: Exception) {
                 _loading.value = false
                 addJourneyDetailMutableLiveData.value = response
             }
         }
    }
}