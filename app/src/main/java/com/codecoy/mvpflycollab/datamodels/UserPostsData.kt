package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class UserPostsData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("intrest_id") var interestId: String? = null,
    @SerializedName("post_desc") var postDesc: String? = null,
    @SerializedName("post_hashtag") var postHashtag: String? = null,
    @SerializedName("lat") var lat: String? = null,
    @SerializedName("long") var long: String? = null,
    @SerializedName("loc_name") var locName: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("user_like_status") var userLikeStatus: String? = null,
    @SerializedName("user_save_status") var userSaveStatus: String? = null,
    @SerializedName("user") var userData: UserDetailsForPost? = UserDetailsForPost(),
    @SerializedName("post_images") var images: ArrayList<UserPostsImages> = arrayListOf(),
    @SerializedName("total_likes") var totalLikes: Int? = null,
    @SerializedName("total_comments") var totalComments: Int? = null
)
