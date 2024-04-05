package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.datamodels.FollowRequestsData

interface RequestsCallback {
    fun onAcceptFollowReq(reqData: FollowRequestsData)
    fun onDeclineFollowReq(reqData: FollowRequestsData)
    fun onAcceptColReq(reqData: FollowRequestsData)
    fun onDeclineColReq(reqData: FollowRequestsData)
}