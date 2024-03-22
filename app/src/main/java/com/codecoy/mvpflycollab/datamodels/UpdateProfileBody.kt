package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class UpdateProfileBody(
    @SerializedName("user_id"       ) var userId       : String? = null,
    @SerializedName("name"          ) var name         : String? = null,
    @SerializedName("phone"         ) var phone        : String? = null,
    @SerializedName("profile_image" ) var profileImage : String? = null
)
