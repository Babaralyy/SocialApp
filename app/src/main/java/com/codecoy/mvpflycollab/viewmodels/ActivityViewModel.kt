package com.codecoy.mvpflycollab.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.AddActivityResponse
import com.codecoy.mvpflycollab.datamodels.AddPlaylistDetailsResponse
import com.codecoy.mvpflycollab.datamodels.AllActivitiesDateResponse
import com.codecoy.mvpflycollab.datamodels.AllActivitiesResponse
import com.codecoy.mvpflycollab.datamodels.AllPlaylistDetailsResponse
import com.codecoy.mvpflycollab.datamodels.UploadImageResponse
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ActivityViewModel (private val mvpRepository: MvpRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var exceptionMutableLiveData = MutableLiveData<Throwable>()
    val exceptionLiveData: LiveData<Throwable> get() = exceptionMutableLiveData

    private val handler = CoroutineExceptionHandler { _, exception ->
        exceptionMutableLiveData.value = exception
        Log.i(Constant.TAG, "addNewPlaylistDetail:: value $exception")
    }

    private val allActivitiesResponseMutableLiveData = MutableLiveData<Response<AllActivitiesResponse>>()
    val allActivitiesResponseLiveData: LiveData<Response<AllActivitiesResponse>> get() = allActivitiesResponseMutableLiveData

    private val imageResponseMutableLiveData = MutableLiveData<Response<UploadImageResponse>>()
    val imageResponseLiveData: LiveData<Response<UploadImageResponse>> get() = imageResponseMutableLiveData

    private val addActivityMutableLiveData = MutableLiveData<Response<AddActivityResponse>>()
    val addActivityResponseLiveData: LiveData<Response<AddActivityResponse>> get() = addActivityMutableLiveData

    private val allPlaylistDetailsResponseMutableLiveData = MutableLiveData<Response<AllPlaylistDetailsResponse>>()
    val allPlaylistDetailsResponseLiveData: LiveData<Response<AllPlaylistDetailsResponse>> get() = allPlaylistDetailsResponseMutableLiveData

    private val addPlaylistDetailsMutableLiveData = MutableLiveData<Response<AddPlaylistDetailsResponse>>()
    val addPlaylistDetailsLiveData: LiveData<Response<AddPlaylistDetailsResponse>> get() = addPlaylistDetailsMutableLiveData

    private val allActivitiesDateMutableLiveData = MutableLiveData<Response<AllActivitiesDateResponse>>()
    val allActivitiesDateLiveData: LiveData<Response<AllActivitiesDateResponse>> get() = allActivitiesDateMutableLiveData



    var mediaImgList: MutableList<Uri> = arrayListOf()
    var mediaVidList: MutableList<Uri> = arrayListOf()

    fun allActivities(token: String,userId: String, activityDate: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.allActivities(token, userId, activityDate)

            try {
                allActivitiesResponseMutableLiveData.value = response

            } catch (e: Exception) {
                allActivitiesResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }
    fun allActivitiesDates(token: String,userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.allActivitiesDates(token, userId)

            try {
                allActivitiesDateMutableLiveData.value = response

            } catch (e: Exception) {
                allActivitiesDateMutableLiveData.value = response
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

    fun addNewActivity(token: String,
                       userId: RequestBody,
                       activityName: RequestBody,
                       activityDescription: RequestBody,
                       activityDate: RequestBody,
                       startTime: RequestBody,
                       endTime: RequestBody,
                       imagesPartList: MutableList<MultipartBody.Part>,
                       videosPartList: MutableList<MultipartBody.Part>){
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.addNewActivity(token, userId, activityName
                ,activityDescription, activityDate, startTime, endTime, imagesPartList, videosPartList)
            try {
                Log.i(TAG, "addNewActivity:: $response")
                addActivityMutableLiveData.value = response

                mediaImgList.clear()
                mediaVidList.clear()

            } catch (e: Exception) {
                addActivityMutableLiveData.value = response

                mediaImgList.clear()
                mediaVidList.clear()

            }finally {
                _loading.value = false
            }
        }
    }


}