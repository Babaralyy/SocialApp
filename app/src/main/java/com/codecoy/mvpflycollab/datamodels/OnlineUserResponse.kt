package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class OnlineUserResponse(
    @SerializedName("data" ) var onlineUserResponseData : OnlineUserResponseData? = OnlineUserResponseData()
)
