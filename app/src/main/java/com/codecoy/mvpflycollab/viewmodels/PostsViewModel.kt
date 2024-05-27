package com.codecoy.mvpflycollab.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecoy.mvpflycollab.datamodels.AddNewPostResponse
import com.codecoy.mvpflycollab.datamodels.CalendarStoryResponse
import com.codecoy.mvpflycollab.datamodels.DeletePostResponse
import com.codecoy.mvpflycollab.datamodels.LikePostResponse
import com.codecoy.mvpflycollab.datamodels.SavePostResponse
import com.codecoy.mvpflycollab.datamodels.UserLevelsResponse
import com.codecoy.mvpflycollab.datamodels.UserPostsResponse
import com.codecoy.mvpflycollab.repo.MvpRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class PostsViewModel(private val mvpRepository: MvpRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var exceptionMutableLiveData = MutableLiveData<Throwable>()
    val exceptionLiveData: LiveData<Throwable> get() = exceptionMutableLiveData

    private val handler = CoroutineExceptionHandler { _, exception ->
        exceptionMutableLiveData.value = exception
    }

    private val allUsersPostsResponseMutableLiveData = MutableLiveData<Response<UserPostsResponse>>()
    val allUsersPostsResponseLiveData: LiveData<Response<UserPostsResponse>> get() = allUsersPostsResponseMutableLiveData

    private val addNewPostResponseMutableLiveData = MutableLiveData<Response<AddNewPostResponse>>()
    val addNewPostResponseLiveData: LiveData<Response<AddNewPostResponse>> get() = addNewPostResponseMutableLiveData

    private val likePostResponseMutableLiveData = MutableLiveData<Response<LikePostResponse>>()
    val likePostResponseLiveData: LiveData<Response<LikePostResponse>> get() = likePostResponseMutableLiveData

    private val allStoriesResponseMutableLiveData = MutableLiveData<Response<CalendarStoryResponse>>()
    val allStoriesResponseLiveData: LiveData<Response<CalendarStoryResponse>> get() = allStoriesResponseMutableLiveData

    private val deletePostResponseMutableLiveData = MutableLiveData<Response<DeletePostResponse>>()
    val deletePostResponseLiveData: LiveData<Response<DeletePostResponse>> get() = deletePostResponseMutableLiveData

    private val savePostResponseMutableLiveData = MutableLiveData<Response<SavePostResponse>>()
    val savePostResponseLiveData: LiveData<Response<SavePostResponse>> get() = savePostResponseMutableLiveData

    private val savedPostsResponseMutableLiveData = MutableLiveData<Response<UserPostsResponse>>()
    val savedUsersPostsResponseLiveData: LiveData<Response<UserPostsResponse>> get() = savedPostsResponseMutableLiveData


    private val levelsResponseMutableLiveData = MutableLiveData<Response<UserLevelsResponse>>()
    val levelsResponseLiveData: LiveData<Response<UserLevelsResponse>> get() = levelsResponseMutableLiveData

    var mediaImgList: MutableList<Uri> = arrayListOf()
    var mediaVidList: MutableList<Uri> = arrayListOf()

    fun allUserPosts(token: String, userId: String, page: Int) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.allUserPosts(token, userId, page)

            try {
                allUsersPostsResponseMutableLiveData.value = response

            } catch (e: Exception) {
                allUsersPostsResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

     fun addNewPost(
        token: String,
        userId: RequestBody,
        category: RequestBody,
        subcategory: RequestBody,
        postDesc: RequestBody,
        postHashtag: RequestBody,
        lat: RequestBody,
        long: RequestBody,
        locName: RequestBody,
        imagesPartList: MutableList<MultipartBody.Part>,
        videosPartList: MutableList<MultipartBody.Part>
    ) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.addNewPost(
                token,
                userId,
                category,
                subcategory,
                postDesc,
                postHashtag,
                lat,
                long,
                locName,
                imagesPartList,
                videosPartList
            )
            try {
                addNewPostResponseMutableLiveData.value = response

            } catch (e: Exception) {
                addNewPostResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

     fun likePost(
        token: String,
        userId: String,
        postId: String,
        date: String,
        time: String
    ) {
        viewModelScope.launch(handler) {
//            _loading.value = true
            val response = mvpRepository.likePost(token, userId, postId, date, time)

            try {
                likePostResponseMutableLiveData.value = response

            } catch (e: Exception) {
                likePostResponseMutableLiveData.value = response
            } finally {
//                _loading.value = false
            }
        }
    }

    fun allStories(token: String, userId: String) {
        viewModelScope.launch(handler) {
//            _loading.value = true
            val response = mvpRepository.allStories(token, userId)

            try {
                allStoriesResponseMutableLiveData.value = response

            } catch (e: Exception) {
                allStoriesResponseMutableLiveData.value = response
            } finally {
//                _loading.value = false
            }
        }
    }

    fun deletePost(token: String, postId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.deletePost(token, postId)

            try {
                deletePostResponseMutableLiveData.value = response

            } catch (e: Exception) {
                deletePostResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun savePost(token: String, userId: String, postId: String) {
        viewModelScope.launch(handler) {
//            _loading.value = true
            val response = mvpRepository.savePost(token, userId, postId)

            try {
                savePostResponseMutableLiveData.value = response

            } catch (e: Exception) {
                savePostResponseMutableLiveData.value = response
            } finally {
//                _loading.value = false
            }
        }
    }

    fun savedPosts(token: String, userId: String) {
        viewModelScope.launch(handler) {
            _loading.value = true
            val response = mvpRepository.savedPosts(token, userId)

            try {
                savedPostsResponseMutableLiveData.value = response

            } catch (e: Exception) {
                savedPostsResponseMutableLiveData.value = response
            } finally {
                _loading.value = false
            }
        }
    }

    fun userLevels(token: String, userId: String) {
        viewModelScope.launch(handler) {
//            _loading.value = true
            val response = mvpRepository.userLevels(token, userId)

            try {
                levelsResponseMutableLiveData.value = response

            } catch (e: Exception) {
                levelsResponseMutableLiveData.value = response
            } finally {
//                _loading.value = false
            }
        }
    }

}