package com.codecoy.mvpflycollab.repo

import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailBody
import com.codecoy.mvpflycollab.datamodels.UpdateProfileBody
import com.codecoy.mvpflycollab.datamodels.UserLoginBody
import com.codecoy.mvpflycollab.datamodels.UserRegisterBody
import com.codecoy.mvpflycollab.network.ApiCall
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header


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

    suspend fun deleteJourney(token: String, journeyId: String) =
        apiCall.deleteJourney(token, journeyId)

    suspend fun allJourneyDetailsList(token: String, journeyId: String) =
        apiCall.allJourneyDetailsList(token, journeyId)

    suspend fun addJourneyDetail(
        token: String,
        journeyId: RequestBody,
        title: RequestBody,
        description: RequestBody,
        date: RequestBody,
        imagesPartList: MutableList<MultipartBody.Part>,
        videosPartList: MutableList<MultipartBody.Part>
    ) =
        apiCall.addJourneyDetail(token, journeyId, title, description, date, imagesPartList, videosPartList)

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
        videosPartList: MutableList<MultipartBody.Part>,
        imagesPartList: MutableList<MultipartBody.Part>
    ) =
        apiCall.addPlaylistDetails(token, playlistId, title, description, date, videosPartList, imagesPartList)

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

    suspend fun allUsers(token: String, userId: String) =
        apiCall.allUsers(token, userId)

    suspend fun followUser(
        token: String,
        userId: String,
        followerId: String,
        date: String,
        time: String
    ) = apiCall.followUser(token, userId, followerId, date, time)

    suspend fun allUserPosts(token: String, userId: String) =
        apiCall.allUserPosts(token, userId)

    suspend fun userProfile(token: String, userId: String) =
        apiCall.userProfile(token, userId)

    suspend fun updateProfile(token: String, updateProfileBody: UpdateProfileBody) =
        apiCall.updateProfile(token, updateProfileBody)

    suspend fun addNewPost(
        token: String,
        userId: RequestBody,
        category: RequestBody,
        subcategory: RequestBody,
        postDesc: RequestBody,
        postHashtag: RequestBody,
        lat: RequestBody,
        long: RequestBody,
        imagesPartList: MutableList<MultipartBody.Part>,
    ) = apiCall.addNewPost(
        token,
        userId,
        category,
        subcategory,
        postDesc,
        postHashtag,
        lat,
        long,
        imagesPartList,
    )

    suspend fun likePost(
        token: String,
        userId: String,
        postId: String,
        date: String,
        time: String
    ) = apiCall.likePost(token, userId, postId, date, time)

    suspend fun allCommentsAgainstPost(token: String, postId: String) =
        apiCall.allCommentsAgainstPost(token, postId)

    suspend fun addComment(
        token: String,
        userId: String,
        postId: String,
        commentTitle: String
    ) = apiCall.addComment(token, userId, postId, commentTitle)

    suspend fun allStories(token: String, userId: String) =
        apiCall.allStories(token, userId)

    suspend fun userFollowing(token: String, userId: String) =
        apiCall.userFollowing(token, userId)

    suspend fun collabUser(
        token: String,
        userId: String,
        collabId: String,
        date: String,
        time: String
    ) = apiCall.collabUser(token, userId, collabId, date, time)

    suspend fun userFollowingRequests(token: String, userId: String) =
        apiCall.userFollowingRequests(token, userId)

    suspend fun userCollabRequests(token: String, userId: String) =
        apiCall.userColRequests(token, userId)

    suspend fun collaboratorsList(token: String, userId: String) =
        apiCall.collaboratorsList(token, userId)

    suspend fun deletePost(token: String, postId: String) =
        apiCall.deletePost(token, postId)

    suspend fun acceptFollowRequest(
        token: String,
        userId: String,
        followerRequestId: String,
        date: String,
        time: String
    ) = apiCall.acceptFollowRequest(token, userId, followerRequestId, date, time)

    suspend fun acceptColRequest(
        token: String,
        userId: String,
        colRequestId: String,
        date: String,
        time: String
    ) = apiCall.acceptColRequest(token, userId, colRequestId, date, time)

    suspend fun declineFollowReq(token: String, userId: String, followerId: String) =
        apiCall.declineFollowReq(token, userId, followerId)

    suspend fun declineColReq(token: String, userId: String, colId: String) =
        apiCall.declineColReq(token, userId, colId)

    suspend fun savePost(token: String, userId: String, postId: String) =
        apiCall.savePost(token, userId, postId)

}