package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AddCommentData(
    @SerializedName("user_id"       ) var userId       : String? = null,
    @SerializedName("post_id"       ) var postId       : String? = null,
    @SerializedName("comment_title" ) var commentTitle : String? = null,
    @SerializedName("updated_at"    ) var updatedAt    : String? = null,
    @SerializedName("created_at"    ) var createdAt    : String? = null,
    @SerializedName("id"            ) var id           : Int?    = null)
