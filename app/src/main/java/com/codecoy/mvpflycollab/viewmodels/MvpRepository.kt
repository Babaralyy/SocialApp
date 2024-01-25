package com.codecoy.mvpflycollab.viewmodels

import com.codecoy.mvpflycollab.datamodels.UserLoginBody
import com.codecoy.mvpflycollab.datamodels.UserRegisterBody
import com.codecoy.mvpflycollab.datamodels.UserRegisterResponse
import com.codecoy.mvpflycollab.network.ApiCall
import okhttp3.MultipartBody
import retrofit2.Response


class MvpRepository(private val apiCall: ApiCall) {
    suspend fun uploadProfileImage(image: MultipartBody.Part) = apiCall.uploadImage(image)
    suspend fun userRegister(userRegisterBody: UserRegisterBody) = apiCall.registerUserResponse(userRegisterBody)
    suspend fun userLogin(userLoginBody: UserLoginBody) = apiCall.userLogin(userLoginBody)
}