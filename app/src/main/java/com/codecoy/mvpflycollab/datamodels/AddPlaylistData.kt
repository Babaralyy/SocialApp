package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AddPlaylistData(
    @SerializedName("user_id"      ) var userId      : String? = null,
    @SerializedName("title"        ) var title       : String? = null,
    @SerializedName("description"  ) var description : String? = null,
    @SerializedName("playlist_img" ) var playlistImg : String? = null,
    @SerializedName("updated_at"   ) var updatedAt   : String? = null,
    @SerializedName("created_at"   ) var createdAt   : String? = null,
    @SerializedName("id"           ) var id          : Int?    = null
)
