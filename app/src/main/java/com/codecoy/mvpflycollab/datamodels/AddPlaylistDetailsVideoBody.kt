package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AddPlaylistDetailsVideoBody(
    @SerializedName("video" ) var video : String? = null
)
