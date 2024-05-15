package com.codecoy.mvpflycollab.network

import android.net.Uri
import com.codecoy.mvpflycollab.datamodels.AcceptColReqResponse
import com.codecoy.mvpflycollab.datamodels.AcceptReqResponse
import com.codecoy.mvpflycollab.datamodels.AddActivityBody
import com.codecoy.mvpflycollab.datamodels.AddActivityResponse
import com.codecoy.mvpflycollab.datamodels.AddCommentResponse
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailBody
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailResponse
import com.codecoy.mvpflycollab.datamodels.AddJourneyResponse
import com.codecoy.mvpflycollab.datamodels.AddNewPostResponse
import com.codecoy.mvpflycollab.datamodels.AddPlaylistDetailsBody
import com.codecoy.mvpflycollab.datamodels.AddPlaylistDetailsResponse
import com.codecoy.mvpflycollab.datamodels.AddPlaylistResponse
import com.codecoy.mvpflycollab.datamodels.AllActivitiesDateResponse
import com.codecoy.mvpflycollab.datamodels.AllActivitiesResponse
import com.codecoy.mvpflycollab.datamodels.AllJourneyResponse
import com.codecoy.mvpflycollab.datamodels.AllPlaylistDetailsResponse
import com.codecoy.mvpflycollab.datamodels.AllPlaylistResponse
import com.codecoy.mvpflycollab.datamodels.AllUserResponse
import com.codecoy.mvpflycollab.datamodels.CalendarStoryResponse
import com.codecoy.mvpflycollab.datamodels.CollaboratorsListResponse
import com.codecoy.mvpflycollab.datamodels.CommentsResponse
import com.codecoy.mvpflycollab.datamodels.DeclineReqResponse
import com.codecoy.mvpflycollab.datamodels.DeleteJourneyMessage
import com.codecoy.mvpflycollab.datamodels.DeletePostResponse
import com.codecoy.mvpflycollab.datamodels.FollowRequestsResponse
import com.codecoy.mvpflycollab.datamodels.FollowUserResponse
import com.codecoy.mvpflycollab.datamodels.JourneyDetailsResponse
import com.codecoy.mvpflycollab.datamodels.LikePostResponse
import com.codecoy.mvpflycollab.datamodels.NotificationResponse
import com.codecoy.mvpflycollab.datamodels.OneTwoOneChatResponse
import com.codecoy.mvpflycollab.datamodels.SavePostResponse
import com.codecoy.mvpflycollab.datamodels.UpdateProfileBody
import com.codecoy.mvpflycollab.datamodels.UpdateProfileResponse
import com.codecoy.mvpflycollab.datamodels.UploadImageResponse
import com.codecoy.mvpflycollab.datamodels.UploadVideoResponse
import com.codecoy.mvpflycollab.datamodels.UserChatListResponse
import com.codecoy.mvpflycollab.datamodels.UserColResponse
import com.codecoy.mvpflycollab.datamodels.UserFollowingResponse
import com.codecoy.mvpflycollab.datamodels.UserLevelsResponse
import com.codecoy.mvpflycollab.datamodels.UserLoginBody
import com.codecoy.mvpflycollab.datamodels.UserLoginResponse
import com.codecoy.mvpflycollab.datamodels.UserPostsResponse
import com.codecoy.mvpflycollab.datamodels.UserProfileData
import com.codecoy.mvpflycollab.datamodels.UserProfileResponse
import com.codecoy.mvpflycollab.datamodels.UserRegisterBody
import com.codecoy.mvpflycollab.datamodels.UserRegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiCall {
    @Multipart
    @POST("api/upload_img")
    suspend fun uploadImage(@Part img: MultipartBody.Part): Response<UploadImageResponse>

    @Multipart
    @POST("api/upload_vedio")
    suspend fun uploadVideo(@Part video: MultipartBody.Part): Response<UploadVideoResponse>

    @POST("api/register")
    suspend fun registerUserResponse(@Body userRegisterBody: UserRegisterBody): Response<UserRegisterResponse>

    @POST("api/login")
    suspend fun userLogin(@Body userLoginBody: UserLoginBody): Response<UserLoginResponse>

    @GET("api/journey_list")
    suspend fun allJourneyList(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String
    ): Response<AllJourneyResponse>

    @FormUrlEncoded
    @POST("api/add_journey")
    suspend fun addJourney(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("journey_img") journeyImg: String
    ): Response<AddJourneyResponse>

    @GET("api/journey_delete")
    suspend fun deleteJourney(
        @Header("Authorization") token: String,
        @Query("journey_id") journeyId: String,
    ): Response<DeleteJourneyMessage>

    @GET("api/journey_details_against_journey")
    suspend fun allJourneyDetailsList(
        @Header("Authorization") token: String,
        @Query("journey_id") journeyId: String
    ): Response<JourneyDetailsResponse>

    @Multipart
    @POST("api/add_journey_details")
    suspend fun addJourneyDetail(
        @Header("Authorization") token: String,
        @Part("journey_id") journeyId: RequestBody,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("date") date: RequestBody,
        @Part imagesPartList: MutableList<MultipartBody.Part>,
        @Part videosPartList: MutableList<MultipartBody.Part>
    ): Response<AddJourneyDetailResponse>

    @GET("api/playlist_list")
    suspend fun allPlayList(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String
    ): Response<AllPlaylistResponse>

    @FormUrlEncoded
    @POST("api/add_playlist")
    suspend fun addPlaylist(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("playlist_img") playlistImg: String
    ): Response<AddPlaylistResponse>

    @GET("api/playlist_details_against_playlist")
    suspend fun allPlaylistDetailsList(
        @Header("Authorization") token: String,
        @Query("playlist_id") playlistId: String
    ): Response<AllPlaylistDetailsResponse>

    @Multipart
    @POST("api/add_playlist_details")
    suspend fun addPlaylistDetails(
        @Header("Authorization") token: String,
        @Part("playlist_id") playlistId: RequestBody,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("date") date: RequestBody,
        @Part videosPartList: MutableList<MultipartBody.Part>,
        @Part imagesPartList: MutableList<MultipartBody.Part>,
    ): Response<AddPlaylistDetailsResponse>

    @GET("api/user_activity_againt_date")
    suspend fun allActivities(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Query("activity_date") activityDate: String,
    ): Response<AllActivitiesResponse>

    @Multipart
    @POST("api/user_activity")
    suspend fun addNewActivity(
        @Header("Authorization") token: String,
        @Part("user_id") userId: RequestBody,
        @Part("activity_name") activityName: RequestBody,
        @Part("activity_description") activityDescription: RequestBody,
        @Part("activity_date") activityDate: RequestBody,
        @Part("start_time") startTime: RequestBody,
        @Part("end_time") endTime: RequestBody,
        @Part imagesPartList: MutableList<MultipartBody.Part>,
        @Part videosPartList: MutableList<MultipartBody.Part>
    ): Response<AddActivityResponse>


    @GET("api/user_activity_list")
    suspend fun allActivitiesDates(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): Response<AllActivitiesDateResponse>

    @GET("api/get_user")
    suspend fun allUsers(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String
    ): Response<AllUserResponse>

    @FormUrlEncoded
    @POST("api/sent_follow_request")
    suspend fun followUser(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("follower_id") followerId: String,
        @Field("date") date: String,
        @Field("time") time: String,
    ): Response<FollowUserResponse>

    @GET("api/user_profile_data")
    suspend fun userProfile(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): Response<UserProfileResponse>

    @POST("api/update_profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body updateProfileBody: UpdateProfileBody
    ): Response<UpdateProfileResponse>

    @GET("api/multiple_posts_user")
    suspend fun allUserPosts(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): Response<UserPostsResponse>

    @Multipart
    @POST("api/user_post")
    suspend fun addNewPost(
        @Header("Authorization") token: String,
        @Part("user_id") userId: RequestBody,
        @Part("category") category: RequestBody,
        @Part("subcategory") subcategory: RequestBody,
        @Part("post_desc") postDesc: RequestBody,
        @Part("post_hashtag") postHashtag: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("long") long: RequestBody,
        @Part("loc_name") locName: RequestBody,
        @Part imagesPartList: MutableList<MultipartBody.Part>,
        @Part videosPartList: MutableList<MultipartBody.Part>
    ): Response<AddNewPostResponse>

    @FormUrlEncoded
    @POST("api/add_like")
    suspend fun likePost(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("post_id") postId: String,
        @Field("date") date: String,
        @Field("time") time: String,
    ): Response<LikePostResponse>

    @FormUrlEncoded
    @POST("api/add_comment")
    suspend fun addComment(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("post_id") postId: String,
        @Field("comment_title") commentTitle: String,
        @Field("date") date: String,
        @Field("time") time: String,
    ): Response<AddCommentResponse>

    @GET("api/comment_list_against_post")
    suspend fun allCommentsAgainstPost(
        @Header("Authorization") token: String,
        @Query("post_id") postId: String,
    ): Response<CommentsResponse>

    @GET("api/user_following_calender_list")
    suspend fun allStories(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): Response<CalendarStoryResponse>
    @GET("api/user_follower_list")
    suspend fun userFollowing(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): Response<UserFollowingResponse>

    @FormUrlEncoded
    @POST("api/sent_collab_request")
    suspend fun collabUser(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("collab_id") collabId: String,
        @Field("date") date: String,
        @Field("time") time: String,
    ): Response<UserColResponse>

    @GET("api/user_follower_req_list")
    suspend fun userFollowingRequests(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): Response<FollowRequestsResponse>

    @GET("api/user_collab_req_list")
    suspend fun userColRequests(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): Response<FollowRequestsResponse>

    @GET("api/user_collab_list")
    suspend fun collaboratorsList(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): Response<CollaboratorsListResponse>

    @GET("api/post_delete")
    suspend fun deletePost(
        @Header("Authorization") token: String,
        @Query("post_id") postId: String,
    ): Response<DeletePostResponse>

    @FormUrlEncoded
    @POST("api/accept_follow_request")
    suspend fun acceptFollowRequest(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("follower_request_id") followerRequestId: String,
        @Field("date") date: String,
        @Field("time") time: String,
    ): Response<AcceptReqResponse>

    @FormUrlEncoded
    @POST("api/accept_collab_request")
    suspend fun acceptColRequest(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("collab_request_id") colRequestId: String,
        @Field("date") date: String,
        @Field("time") time: String,
    ): Response<AcceptColReqResponse>

    @GET("api/decline_follow_request")
    suspend fun declineFollowReq(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Query("follower_request_id") followerId: String
    ): Response<DeclineReqResponse>

    @GET("api/decline_collab_request")
    suspend fun declineColReq(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Query("collab_request_id") colId: String
    ): Response<DeclineReqResponse>

    @FormUrlEncoded
    @POST("api/save_post")
    suspend fun savePost(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("post_id") postId: String,
    ): Response<SavePostResponse>

    @GET("api/save_post_list_user")
    suspend fun savedPosts(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): Response<UserPostsResponse>

    @GET("api/chat_list_user")
    suspend fun userChatList(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): Response<UserChatListResponse>

    @GET("api/message_list")
    suspend fun oneTwoOneChat(
        @Header("Authorization") token: String,
        @Query("sender_id") senderId: String,
        @Query("receiver_id") receiverId: String,
    ): Response<OneTwoOneChatResponse>

    @GET("api/notification")
    suspend fun notifications(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): Response<NotificationResponse>

    @GET("api/update_levels")
    suspend fun userLevels(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): Response<UserLevelsResponse>

}