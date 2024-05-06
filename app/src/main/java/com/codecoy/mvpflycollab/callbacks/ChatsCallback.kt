package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.datamodels.UserChatListData
import com.codecoy.mvpflycollab.datamodels.UserFollowingData

interface ChatsCallback {
    fun onUserClick(chatData: UserChatListData){}
    fun onFollowerClick(chatData: UserFollowingData){}
}