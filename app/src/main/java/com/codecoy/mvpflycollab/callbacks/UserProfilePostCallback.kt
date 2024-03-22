package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.datamodels.UserProfilePosts

interface UserProfilePostCallback {
    fun onUserProfilePostClick(post: UserProfilePosts)
}