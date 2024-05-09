package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class NotificationListData(@SerializedName("id"         ) var id        : Int?    = null,
                                @SerializedName("user_id"    ) var userId    : Int?    = null,
                                @SerializedName("post_id"    ) var postId    : Int?    = null,
                                @SerializedName("date"       ) var date      : String? = null,
                                @SerializedName("time"       ) var time      : String? = null,
                                @SerializedName("created_at" ) var createdAt : String? = null,
                                @SerializedName("updated_at" ) var updatedAt : String? = null)
