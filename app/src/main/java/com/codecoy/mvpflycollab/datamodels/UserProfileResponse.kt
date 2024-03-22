package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("success"         ) var success        : Boolean?         = null,
    @SerializedName("journey_count"   ) var journeyCount   : Int?             = null,
    @SerializedName("playlist_count"  ) var playlistCount  : Int?             = null,
    @SerializedName("followers_count" ) var followersCount : Int?             = null,
    @SerializedName("user"            ) var user           : UserProfileData?            = UserProfileData(),
    @SerializedName("posts"           ) var posts          : ArrayList<UserProfilePosts> = arrayListOf()
)
