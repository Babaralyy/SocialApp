package com.codecoy.mvpflycollab.repo

import com.codecoy.mvpflycollab.datamodels.AddActivityBody
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailBody
import com.codecoy.mvpflycollab.datamodels.UserLoginBody
import com.codecoy.mvpflycollab.datamodels.UserRegisterBody
import com.codecoy.mvpflycollab.network.ApiCall
import okhttp3.MultipartBody
import okhttp3.RequestBody


class MvpRepository(private val apiCall: ApiCall) {
    suspend fun uploadProfileImage(image: MultipartBody.Part) = apiCall.uploadImage(image)
    suspend fun uploadVideo(video: MultipartBody.Part) = apiCall.uploadVideo(video)
    suspend fun userRegister(userRegisterBody: UserRegisterBody) =
        apiCall.registerUserResponse(userRegisterBody)

    suspend fun userLogin(userLoginBody: UserLoginBody) = apiCall.userLogin(userLoginBody)
    suspend fun allJourneyList(token: String, userId: String) =
        apiCall.allJourneyList(token, userId)

    suspend fun addJourney(
        token: String,
        userId: String,
        title: String,
        description: String,
        journeyImg: String
    ) = apiCall.addJourney(token, userId, title, description, journeyImg)

    suspend fun allJourneyDetailsList(token: String, journeyId: String) =
        apiCall.allJourneyDetailsList(token, journeyId)

    suspend fun addJourneyDetail(token: String, addJourneyDetailBody: AddJourneyDetailBody) =
        apiCall.addJourneyDetail(token, addJourneyDetailBody)

    suspend fun allPlayList(token: String, userId: String) = apiCall.allPlayList(token, userId)
    suspend fun addPlaylist(
        token: String,
        userId: String,
        title: String,
        description: String,
        playListImg: String
    ) = apiCall.addPlaylist(token, userId, title, description, playListImg)

    suspend fun allPlaylistDetailsList(token: String, playlistId: String) =
        apiCall.allPlaylistDetailsList(token, playlistId)


    suspend fun addPlaylistDetails(
        token: String,
        playlistId: RequestBody,
        title: RequestBody,
        description: RequestBody,
        date: RequestBody,
        videosPartList: MutableList<MultipartBody.Part>
    ) =
        apiCall.addPlaylistDetails(token, playlistId, title, description, date, videosPartList)

    suspend fun allActivities(token: String, userId: String, activityDate: String) =
        apiCall.allActivities(token, userId, activityDate)

    suspend fun addNewActivity(
        token: String,
        userId: RequestBody,
        activityName: RequestBody,
        activityDescription: RequestBody,
        activityDate: RequestBody,
        startTime: RequestBody,
        endTime: RequestBody,
        imagesPartList: MutableList<MultipartBody.Part>,
        videosPartList: MutableList<MultipartBody.Part>
    ) = apiCall.addNewActivity(
        token,
        userId,
        activityName,
        activityDescription,
        activityDate,
        startTime,
        endTime,
        imagesPartList,
        videosPartList
    )

    suspend fun allActivitiesDates(token: String, userId: String) =
        apiCall.allActivitiesDates(token, userId)
    suspend fun allUsers(token: String) =
        apiCall.allUsers(token)
    suspend fun followUser(
        token: String,
        userId: String,
        followerId: String,
        date: String,
        time: String
    ) = apiCall.followUser(token, userId, followerId, date, time)

    suspend fun allUserPosts(token: String, userId: String) =
        apiCall.allUserPosts(token, userId)
}