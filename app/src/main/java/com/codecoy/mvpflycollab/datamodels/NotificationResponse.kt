package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("user") var user: NotificationUserData? = NotificationUserData(),
    @SerializedName("notification_list") var notificationList: ArrayList<NotificationListData> = arrayListOf()
)
