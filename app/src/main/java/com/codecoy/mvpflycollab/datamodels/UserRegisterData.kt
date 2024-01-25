package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class UserRegisterData(
    @SerializedName("name") var name: String? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("id") var id: Int? = null
)
