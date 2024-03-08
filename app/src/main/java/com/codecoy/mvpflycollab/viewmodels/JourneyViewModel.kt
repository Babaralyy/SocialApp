package com.codecoy.mvpflycollab.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailBody
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailResponse
import com.codecoy.mvpflycollab.datamodels.AddJourneyResponse
import com.codecoy.mvpflycollab.datamodels.AllJourneyResponse
import com.codecoy.mvpflycollab.datamodels.JourneyDetailsResponse
import com.codecoy.mvpflycollab.datamodels.UploadImageResponse
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.utils.Constant.TAG
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


    private val journeyDetailsResponseMutableLiveData = MutableLiveData<Response<JourneyDetailsResponse>>()
    val  journeyDetailsResponseLiveData: LiveData<Response<JourneyDetailsResponse>> get() = journeyDetailsResponseMutableLiveData

    private val addJourneyDetailMutableLiveData = MutableLiveData<Response<AddJourneyDetailResponse>>()
    val addJourneyDetailLiveData: LiveData<Response<AddJourneyDetailResponse>> get() = addJourneyDetailMutableLiveData

    fun allJourneyList(token: String,userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.allJourneyList(token, userId)

            try {
                allJourneyResponseMutableLiveData.value = response

            } catch (e: Exception) {
                allJourneyResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun uploadImage(image: MultipartBody.Part){
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.uploadProfileImage(image)

            Log.i(TAG, "uploadImage:: ${response.body()} ")

            try {
                imageResponseMutableLiveData.value = response

            } catch (e: Exception) {
                imageResponseMutableLiveData.value = response
            }finally {
                _loading.value = false
            }
        }
    }

    fun addJourney(token: String, userId: String, title: String, description: String, journeyImg: String){
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.addJourney(token, userId, title, description, journeyImg)

            try {
                addJourneyResponseMutableLiveData.value = response

            } catch (e: Exception) {
                addJourneyResponseMutableLiveData.value = response
            }finally {
                _loading.value = false
            }
        }
    }


    fun allJourneyDetailsList(token: String, journeyId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.allJourneyDetailsList(token, journeyId)

            try {
                journeyDetailsResponseMutableLiveData.value = response

            } catch (e: Exception) {

                journeyDetailsResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

     fun addJourneyDetail(token: String, addJourneyDetailBody: AddJourneyDetailBody){
         viewModelScope.launch(handler) {
             _loading.value = true
             val response = mvpRepository.addJourneyDetail(token, addJourneyDetailBody)

             try {
                 addJourneyDetailMutableLiveData.value = response

             } catch (e: Exception) {
                 addJourneyDetailMutableLiveData.value = response
             } finally {
                 _loading.value = false
             }
         }
    }
}