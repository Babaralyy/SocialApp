package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class OneTwoOneChatResponse(
    @SerializedName("success"  ) var success  : Boolean?            = null,
    @SerializedName("message"  ) var message  : String?             = null,
    @SerializedName("response" ) var oneTwoOneChatData : ArrayList<OneTwoOneChatData> = arrayListOf()
)
