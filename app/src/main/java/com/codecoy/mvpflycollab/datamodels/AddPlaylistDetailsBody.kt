package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AddPlaylistDetailsBody(
    @SerializedName("playlist_id" ) var playlistId  : Int?              = null,
    @SerializedName("title"       ) var title       : String?           = null,
    @SerializedName("description" ) var description : String?           = null,
    @SerializedName("date"        ) var date        : String?           = null,
    @SerializedName("videos"      ) var videos      : ArrayList<AddPlaylistDetailsVideoBody> = arrayListOf()
)
