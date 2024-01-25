package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class UserRegisterResponse(
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: UserRegisterData? = UserRegisterData()
)
