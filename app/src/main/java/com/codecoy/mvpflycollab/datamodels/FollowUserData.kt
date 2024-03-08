package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class FollowUserData(
    @SerializedName("user_id"     ) var userId     : String? = null,
    @SerializedName("follower_id" ) var followerId : String? = null,
    @SerializedName("date"        ) var date       : String? = null,
    @SerializedName("time"        ) var time       : String? = null,
    @SerializedName("updated_at"  ) var updatedAt  : String? = null,
    @SerializedName("created_at"  ) var createdAt  : String? = null,
    @SerializedName("id"          ) var id         : Int?    = null
)
