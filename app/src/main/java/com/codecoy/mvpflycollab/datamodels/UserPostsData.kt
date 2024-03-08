package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class UserPostsData(
    @SerializedName("id"           ) var id          : Int?              = null,
    @SerializedName("user_id"      ) var userId      : Int?              = null,
    @SerializedName("intrest_id"   ) var interestId   : Int?              = null,
    @SerializedName("post_desc"    ) var postDesc    : String?           = null,
    @SerializedName("post_hashtag" ) var postHashtag : String?           = null,
    @SerializedName("lat"          ) var lat         : String?           = null,
    @SerializedName("long"         ) var long        : String?           = null,
    @SerializedName("created_at"   ) var createdAt   : String?           = null,
    @SerializedName("updated_at"   ) var updatedAt   : String?           = null,
    @SerializedName("images"       ) var images      : ArrayList<UserPostsImages> = arrayListOf()
)
