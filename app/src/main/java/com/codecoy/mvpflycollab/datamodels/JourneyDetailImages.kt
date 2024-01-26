package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class JourneyDetailImages(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("jd_id") var jdId: Int? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null
)
