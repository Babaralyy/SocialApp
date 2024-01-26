package com.codecoy.mvpflycollab.viewmodels

import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailBody
import com.codecoy.mvpflycollab.datamodels.UserLoginBody
import com.codecoy.mvpflycollab.datamodels.UserRegisterBody
import com.codecoy.mvpflycollab.datamodels.UserRegisterResponse
import com.codecoy.mvpflycollab.network.ApiCall
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Field


class MvpRepository(private val apiCall: ApiCall) {
    suspend fun uploadProfileImage(image: MultipartBody.Part) = apiCall.uploadImage(image)
    suspend fun userRegister(userRegisterBody: UserRegisterBody) = apiCall.registerUserResponse(userRegisterBody)
    suspend fun userLogin(userLoginBody: UserLoginBody) = apiCall.userLogin(userLoginBody)
    suspend fun allJourneyList(token: String, userId: String) = apiCall.allJourneyList(token, userId)
    suspend fun addJourney(token: String, userId: String, title: String, description: String, journeyImg: String) = apiCall.addJourney(token, userId, title, description, journeyImg)
    suspend fun addJourneyDetail(token: String, addJourneyDetailBody: AddJourneyDetailBody) = apiCall.addJourneyDetail(token, addJourneyDetailBody)
}