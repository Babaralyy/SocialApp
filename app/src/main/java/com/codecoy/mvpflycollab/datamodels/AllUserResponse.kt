package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AllUserResponse(
    @SerializedName("success"   ) var success  : Boolean?            = null,
    @SerializedName("message"   ) var message  : String?             = null,
    @SerializedName("user_list" ) var userList : ArrayList<AllUserData> = arrayListOf()
)
