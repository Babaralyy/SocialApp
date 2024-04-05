package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class UserLoginData(
    @SerializedName("id"                ) var id              : Int?    = null,
    @SerializedName("profile_img"       ) var profileImg      : String? = null,
    @SerializedName("name"              ) var name            : String? = null,
    @SerializedName("username"          ) var username        : String? = null,
    @SerializedName("phone"             ) var phone           : String? = null,
    @SerializedName("email"             ) var email           : String? = null,
    @SerializedName("email_verified_at" ) var emailVerifiedAt : String? = null,
    @SerializedName("device_token"      ) var deviceToken     : String? = null,
    @SerializedName("website_url"       ) var websiteUrl      : String? = null,
    @SerializedName("about_me"          ) var aboutMe         : String? = null,
    @SerializedName("created_at"        ) var createdAt       : String? = null,
    @SerializedName("updated_at"        ) var updatedAt       : String? = null,
    @SerializedName("token"             ) var token           : String? = null
)
