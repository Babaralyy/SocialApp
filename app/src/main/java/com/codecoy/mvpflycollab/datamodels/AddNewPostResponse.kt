package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AddNewPostResponse(@SerializedName("success" ) var success : Boolean? = null,
                              @SerializedName("message" ) var message : String?  = null)
