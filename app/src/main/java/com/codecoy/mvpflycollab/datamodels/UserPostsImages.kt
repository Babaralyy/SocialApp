package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class UserPostsImages(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("post_id") var postId: Int? = null,
    @SerializedName("url") var postImg: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null
)
