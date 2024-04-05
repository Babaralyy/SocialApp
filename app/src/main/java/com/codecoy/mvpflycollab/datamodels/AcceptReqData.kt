package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AcceptReqData(
    @SerializedName("id"                ) var id              : Int?    = null,
    @SerializedName("user_id"           ) var userId          : String? = null,
    @SerializedName("follower_id"       ) var followerId      : String? = null,
    @SerializedName("collab_id"         ) var collabId        : String? = null,
    @SerializedName("follow_request_id" ) var followRequestId : String? = null,
    @SerializedName("collab_request_id" ) var collabRequestId : String? = null,
    @SerializedName("follow_status"     ) var followStatus    : String? = null,
    @SerializedName("collab_status"     ) var collabStatus    : String? = null,
    @SerializedName("date"              ) var date            : String? = null,
    @SerializedName("time"              ) var time            : String? = null,
    @SerializedName("created_at"        ) var createdAt       : String? = null,
    @SerializedName("updated_at"        ) var updatedAt       : String? = null
)
