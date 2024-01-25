package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class UserLoginBody(
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("device_token") var deviceToken: String? = null
)
