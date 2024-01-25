package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class UserRegisterBody(
    @SerializedName("profile_image") var profileImage: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null
)