package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.datamodels.AllUserData

interface UserFollowCallback {
    fun onFollowClick(user: AllUserData){}
    fun onCollabClick(user: AllUserData){}
    fun onImageClick(user: AllUserData){}
    fun onNameClick(user: AllUserData){}
}