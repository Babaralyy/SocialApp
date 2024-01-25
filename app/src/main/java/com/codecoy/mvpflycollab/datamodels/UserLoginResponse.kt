package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class UserLoginResponse(
    @SerializedName("success" ) var success : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("user"    ) var user    : UserLoginData?    = UserLoginData()
)
