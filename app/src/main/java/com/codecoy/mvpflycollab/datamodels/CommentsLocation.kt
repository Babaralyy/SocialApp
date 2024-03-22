package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class CommentsLocation(@SerializedName("lat"  ) var lat  : String? = null,
                            @SerializedName("long" ) var long : String? = null)
