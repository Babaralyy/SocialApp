package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AllPlaylistResponse(
    @SerializedName("success"  ) var success  : Boolean?            = null,
    @SerializedName("message"  ) var message  : String?             = null,
    @SerializedName("response" ) var allPlaylistData : ArrayList<AllPlaylistData> = arrayListOf()
)
