package com.codecoy.mvpflycollab.network

import com.codecoy.mvpflycollab.datamodels.UploadImageResponse
import com.codecoy.mvpflycollab.datamodels.UserLoginBody
import com.codecoy.mvpflycollab.datamodels.UserLoginResponse
import com.codecoy.mvpflycollab.datamodels.UserRegisterBody
import com.codecoy.mvpflycollab.datamodels.UserRegisterResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiCall {
    @Multipart
    @POST("api/upload_img")
    suspend fun uploadImage(@Part img: MultipartBody.Part ): Response<UploadImageResponse>
    @POST("api/register")
    suspend fun registerUserResponse(@Body userRegisterBody: UserRegisterBody): Response<UserRegisterResponse>
    @POST("api/login")
    suspend fun userLogin(@Body userLoginBody: UserLoginBody): Response<UserLoginResponse>

}