package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AllPlaylistDetailsData(
    @SerializedName("id"          ) var id          : Int?              = null,
    @SerializedName("playlist_id" ) var playlistId  : Int?              = null,
    @SerializedName("title"       ) var title       : String?           = null,
    @SerializedName("description" ) var description : String?           = null,
    @SerializedName("date"        ) var date        : String?           = null,
    @SerializedName("created_at"  ) var createdAt   : String?           = null,
    @SerializedName("updated_at"  ) var updatedAt   : String?           = null,
    @SerializedName("videos"      ) var videos      : ArrayList<AllPlaylistDetailVideos> = arrayListOf()
)
