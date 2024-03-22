package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AllPlaylistDetailsResponse(
    @SerializedName("success"            ) var success          : Boolean?            = null,
    @SerializedName("message"            ) var message          : String?             = null,
    @SerializedName("playlist_title_name" ) var playlistTitleName  : String?             = null,
    @SerializedName("response"           ) var response         : ArrayList<AllPlaylistDetailsData> = arrayListOf()
)
