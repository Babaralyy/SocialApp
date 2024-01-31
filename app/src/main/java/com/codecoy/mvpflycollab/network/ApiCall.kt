package com.codecoy.mvpflycollab.network

import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailBody
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailResponse
import com.codecoy.mvpflycollab.datamodels.AddJourneyResponse
import com.codecoy.mvpflycollab.datamodels.AllJourneyResponse
import com.codecoy.mvpflycollab.datamodels.JourneyDetailsResponse
import com.codecoy.mvpflycollab.datamodels.UploadImageResponse
import com.codecoy.mvpflycollab.datamodels.UserLoginBody
import com.codecoy.mvpflycollab.datamodels.UserLoginResponse
import com.codecoy.mvpflycollab.datamodels.UserRegisterBody
import com.codecoy.mvpflycollab.datamodels.UserRegisterResponse
import okhttp3.MultipartBody
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

}