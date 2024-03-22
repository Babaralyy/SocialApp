package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AllPlaylistDetailVideos(
    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("pd_id"      ) var pdId      : String? = null,
    @SerializedName("img_url"    ) var imgUrl    : String? = null,
    @SerializedName("video_url"  ) var videoUrl  : String? = null,
    @SerializedName("created_at" ) var createdAt : String? = null,
    @SerializedName("updated_at" ) var updatedAt : String? = null
)
