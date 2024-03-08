package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.datamodels.AllUserData

interface UserFollowCallback {
    fun onFollowClick(user: AllUserData)
}