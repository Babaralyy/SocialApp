package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AddPlaylistDetailsResponse(
    @SerializedName("success"  ) var success  : Boolean?  = null,
    @SerializedName("message"  ) var message  : String?   = null,
    @SerializedName("response" ) var addPlaylistDetailsData : AddPlaylistDetailsData? = AddPlaylistDetailsData(),
    @SerializedName("path" ) var path : AddPlaylistDetailsData? = AddPlaylistDetailsData()
)
