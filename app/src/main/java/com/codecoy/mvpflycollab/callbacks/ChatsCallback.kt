package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.datamodels.OnlineUserData

interface ChatsCallback {
    fun onUserClick(chatData: OnlineUserData)
}