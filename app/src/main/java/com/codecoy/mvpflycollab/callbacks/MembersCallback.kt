package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.datamodels.CollaboratorsListData

interface MembersCallback {
    fun onMemberClick(memberData: CollaboratorsListData)
}