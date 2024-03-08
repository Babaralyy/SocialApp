package com.codecoy.mvpflycollab.network

import android.net.Uri
import com.codecoy.mvpflycollab.datamodels.AddActivityBody
import com.codecoy.mvpflycollab.datamodels.AddActivityResponse
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailBody
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailResponse
import com.codecoy.mvpflycollab.datamodels.AddJourneyResponse
import com.codecoy.mvpflycollab.datamodels.AddPlaylistDetailsBody
import com.codecoy.mvpflycollab.datamodels.AddPlaylistDetailsResponse
import com.codecoy.mvpflycollab.datamodels.AddPlaylistResponse
import com.codecoy.mvpflycollab.datamodels.AllActivitiesDateResponse
import com.codecoy.mvpflycollab.datamodels.AllActivitiesResponse
import com.codecoy.mvpflycollab.datamodels.AllJourneyResponse
import com.codecoy.mvpflycollab.datamodels.AllPlaylistDetailsResponse
import com.codecoy.mvpflycollab.datamodels.AllPlaylistResponse
import com.codecoy.mvpflycollab.datamodels.AllUserResponse
import com.codecoy.mvpflycollab.datamodels.FollowUserResponse
import com.codecoy.mvpflycollab.datamodels.JourneyDetailsResponse
import com.codecoy.mvpflycollab.datamodels.UploadImageResponse
import com.codecoy.mvpflycollab.datamodels.UploadVideoResponse
import com.codecoy.mvpflycollab.datamodels.UserLoginBody
import com.codecoy.mvpflycollab.datamodels.UserLoginResponse
import com.codecoy.mvpflycollab.datamodels.UserPostsResponse
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

    @GET("api/journey_details_against_journey")
    suspend fun allJourneyDetailsList(
        @Header("Authorization") token: String,
        @Query("journey_id") journeyId: String
    ): Response<JourneyDetailsResponse>

    @POST("api/add_journey_details")
    suspend fun addJourneyDetail(
        @Header("Authorization") token: String,
        @Body addJourneyDetailBody: AddJourneyDetailBody
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
            @Part videosPartList: MutableList<MultipartBody.Part>
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
        @Header("Authorization") token: String
    ): Response<AllUserResponse>

    @FormUrlEncoded
    @POST("api/follow")
    suspend fun followUser(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("follower_id") followerId: String,
        @Field("date") date: String,
        @Field("time") time: String,
    ): Response<FollowUserResponse>

    @GET("api/multiple_posts_user")
    suspend fun allUserPosts(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): Response<UserPostsResponse>

}