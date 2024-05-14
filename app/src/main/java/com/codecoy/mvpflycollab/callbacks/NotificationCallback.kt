package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.datamodels.NotificationListData

interface NotificationCallback {
    fun onNotificationClick(notData: NotificationListData)
}