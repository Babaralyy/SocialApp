package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class UserGotOnlineResponse(
    @SerializedName("data" ) var data : UserGotOnlineData? = UserGotOnlineData()
)
