package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AllPlaylistDetailVideos(
    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("pd_id"      ) var pdId      : Int?    = null,
    @SerializedName("video"      ) var video     : String? = null,
    @SerializedName("created_at" ) var createdAt : String? = null,
    @SerializedName("updated_at" ) var updatedAt : String? = null
)
