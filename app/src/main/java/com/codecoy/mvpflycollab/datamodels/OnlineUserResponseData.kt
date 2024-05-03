package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class OnlineUserResponseData(
    @SerializedName("success"  ) var success  : Boolean?  = null,
    @SerializedName("message"  ) var message  : String?   = null,
    @SerializedName("response" ) var response : OnlineUserData? = OnlineUserData()
)
