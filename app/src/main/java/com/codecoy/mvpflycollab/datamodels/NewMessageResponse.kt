package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class NewMessageResponse(
    @SerializedName("data" ) var data : NewMessageData? = NewMessageData()
)
