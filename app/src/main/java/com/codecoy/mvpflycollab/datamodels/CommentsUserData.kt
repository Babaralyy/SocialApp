package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class CommentsUserData(
    @SerializedName("name") var name: String? = null,
    @SerializedName("profile_img") var profileImg: String? = null
)
