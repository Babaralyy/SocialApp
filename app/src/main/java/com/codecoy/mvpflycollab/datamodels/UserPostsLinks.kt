package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class UserPostsLinks(
    @SerializedName("url"    ) var url    : String?  = null,
    @SerializedName("label"  ) var label  : String?  = null,
    @SerializedName("active" ) var active : Boolean? = null

)
