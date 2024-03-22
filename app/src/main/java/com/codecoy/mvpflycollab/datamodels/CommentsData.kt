package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class CommentsData(
    @SerializedName("id"            ) var id           : Int?      = null,
    @SerializedName("user_id"       ) var userId       : String?   = null,
    @SerializedName("post_id"       ) var postId       : String?   = null,
    @SerializedName("comment_title" ) var commentTitle : String?   = null,
    @SerializedName("created_at"    ) var createdAt    : String?   = null,
    @SerializedName("updated_at"    ) var updatedAt    : String?   = null,
    @SerializedName("location"      ) var location     : CommentsLocation? = CommentsLocation(),
    @SerializedName("user_data"     ) var userData     : CommentsUserData? = CommentsUserData()

)
